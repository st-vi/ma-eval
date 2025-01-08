package org.example;

import de.vill.model.*;
import de.vill.model.constraint.Constraint;
import de.vill.model.constraint.GreaterEqualsEquationConstraint;
import de.vill.model.expression.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ModelGenerator {
    public static void main(String[] args) throws IOException {
        ModelGenerator modelGenerator = new ModelGenerator();
/*
        List<FeatureModel> alternativeModels = modelGenerator.generateAlternativeModels(5, 100, 5);
        Path modelDirAlternative = Paths.get("/home/stefan/stefan-vill-master/eval/iso_models/alternative");
        modelGenerator.safeFeatureModelsToFiles(alternativeModels, modelDirAlternative);

        List<FeatureModel> groupCardinalityModels = modelGenerator.generateGroupCardinalityModels(List.of(0.0, 0.25, 0.5, 0.75, 1.0), 15, 30, 5);
        Path modelDirGroupCard = Paths.get("/home/stefan/stefan-vill-master/eval/iso_models/groupCard");
        modelGenerator.safeFeatureModelsToFiles(groupCardinalityModels, modelDirGroupCard);


 */
        List<FeatureModel> featureCardinalityModels = modelGenerator.generateFeatureCardinalityModels(List.of(0.0, 0.25, 0.5, 0.75, 1.0), 15, 30, 5);
        Path modelDirFeatureCard = Paths.get("/home/stefan/stefan-vill-master/eval/iso_models/featureCard");
        modelGenerator.safeFeatureModelsToFiles(featureCardinalityModels, modelDirFeatureCard);
/*
        List<FeatureModel> sumModels = modelGenerator.generateSumModels(15, 30, 5, 0, 100);
        Path modelDirSum = Paths.get("/home/stefan/stefan-vill-master/eval/iso_models/sum");
        modelGenerator.safeFeatureModelsToFiles(sumModels, modelDirSum);

        List<FeatureModel> productModels = modelGenerator.generateProductModels(15, 30, 5, 0, 100);
        Path modelDirProduct = Paths.get("/home/stefan/stefan-vill-master/eval/iso_models/product");
        modelGenerator.safeFeatureModelsToFiles(productModels, modelDirProduct);

        List<FeatureModel> divModels = modelGenerator.generateDivModels(15, 30, 5, 0, 100);
        Path modelDirDiv = Paths.get("/home/stefan/stefan-vill-master/eval/iso_models/div");
        modelGenerator.safeFeatureModelsToFiles(divModels, modelDirDiv);

        List<FeatureModel> divModels2 = modelGenerator.generateDivModels2(15, 30, 5, 0, 100);
        Path modelDirDiv2 = Paths.get("/home/stefan/stefan-vill-master/eval/iso_models/div2");
        modelGenerator.safeFeatureModelsToFiles(divModels2, modelDirDiv2);


 */


        //FeatureModel test = modelGenerator.generateDivModel2(10, 0, 100);
        //System.out.println(test.toString());
    }

    private List<FeatureModel> generateDivModels2(int minN, int maxN, int stepSize, int aMin, int aMax) {
        List<FeatureModel> generatedModelList = new LinkedList<>();
        for(int n=minN;n<=maxN;n+=stepSize){
            generatedModelList.add(generateDivModel2(n,aMin,aMax));
        }
        return generatedModelList;
    }

    private List<FeatureModel> generateDivModels(int minN, int maxN, int stepSize, int aMin, int aMax) {
        List<FeatureModel> generatedModelList = new LinkedList<>();
        for(int n=minN;n<=maxN;n+=stepSize){
            generatedModelList.add(generateDivModel(n,aMin,aMax));
        }
        return generatedModelList;
    }

    private List<FeatureModel> generateProductModels(int minN, int maxN, int stepSize, int aMin, int aMax) {
        List<FeatureModel> generatedModelList = new LinkedList<>();
        for(int n=minN;n<=maxN;n+=stepSize){
            generatedModelList.add(generateProductModel(n,aMin,aMax));
        }
        return generatedModelList;
    }

    private List<FeatureModel> generateSumModels(int minN, int maxN, int stepSize, int aMin, int aMax) {
        List<FeatureModel> generatedModelList = new LinkedList<>();
        for(int n=minN;n<=maxN;n+=stepSize){
            generatedModelList.add(generateAdditionModel(n,aMin,aMax));
        }
        return generatedModelList;
    }

    private List<FeatureModel> generateGroupCardinalityModels(List<Double> cardinalities, int minNumberChildren, int maxNumberChildren, int stepSize) {
        List<FeatureModel> generatedModelList = new LinkedList<>();
        for (int numberChildren = minNumberChildren;numberChildren<=maxNumberChildren;numberChildren+=stepSize){
            for(int minIndex=0;minIndex<cardinalities.size()-1;minIndex++){
                for(int maxIndex=minIndex+1;maxIndex<cardinalities.size();maxIndex++){
                    generatedModelList.add(generateGroupCardinalityModel(numberChildren, minIndex == 0 ? 1 : (int) Math.ceil(numberChildren * cardinalities.get(minIndex)), (int) Math.ceil(numberChildren * cardinalities.get(maxIndex))));
                }
            }
        }
        return generatedModelList;
    }

    private List<FeatureModel> generateFeatureCardinalityModels(List<Double> cardinalities, int minN, int maxN, int stepSize) {
        List<FeatureModel> generatedModelList = new LinkedList<>();
        for (int numberChildren = minN;numberChildren<=maxN;numberChildren+=stepSize){
            for(int minIndex=0;minIndex<cardinalities.size()-1;minIndex++){
                for(int maxIndex=minIndex+1;maxIndex<cardinalities.size();maxIndex++){
                    generatedModelList.add(generateFeatureCardinalityModel(minIndex == 0 ? 1 : (int) Math.ceil(numberChildren * cardinalities.get(minIndex)), (int) Math.ceil(numberChildren * cardinalities.get(maxIndex))));
                }
            }
        }
        return generatedModelList;
    }

    private List<FeatureModel> generateAlternativeModels(int minN, int maxN, int stepSize) {
        List<FeatureModel> generatedModelList = new LinkedList<>();
        for (int numberChildren = minN;numberChildren<=maxN;numberChildren+=stepSize){
            generatedModelList.add(generateAlternativeModel(numberChildren));
        }
        return generatedModelList;
    }


    private FeatureModel generateAlternativeModel(int n){
        FeatureModel featureModel = new FeatureModel();
        Feature rootFeature = new Feature("root_" + n);
        featureModel.setRootFeature(rootFeature);
        Group group = new Group(Group.GroupType.ALTERNATIVE);
        rootFeature.getChildren().add(group);

        for(int i=1;i<=n;i++){
            Feature feature = new Feature("feature" + i);
            feature.setParentGroup(group);
            group.getFeatures().add(feature);
        }

        return featureModel;
    }

    private FeatureModel generateDivModel2(int n, int aMin, int aMax){
        FeatureModel featureModel = new FeatureModel();
        Feature rootFeature = new Feature("root_" + n);
        featureModel.setRootFeature(rootFeature);
        Group group = new Group(Group.GroupType.OPTIONAL);
        rootFeature.getChildren().add(group);

        for(int i=1;i<=n;i++){
            Feature feature = new Feature("feature" + i);
            var attributes = feature.getAttributes();
            Random random = new Random();
            int value = random.nextInt(aMax - aMin + 1) + aMin;
            attributes.put("a", new Attribute<Long>("a", (long)value, feature));
            feature.setParentGroup(group);
            group.getFeatures().add(feature);
        }

        int nd2 = (group.getFeatures().size() - 1) / 2;
        Expression d1 = new ParenthesisExpression(new AddExpression(new LiteralExpression(group.getFeatures().get(0).getAttributes().get("a")), new LiteralExpression(group.getFeatures().get(1).getAttributes().get("a"))));
        Expression d2 = new ParenthesisExpression(new MulExpression(new LiteralExpression(group.getFeatures().get(nd2+1).getAttributes().get("a")), new LiteralExpression(group.getFeatures().get(nd2+2).getAttributes().get("a"))));

        long a1 = 1;
        for(int i=2;i<=nd2;i++){
            Feature feature = group.getFeatures().get(i);
            LiteralExpression literalExpression = new LiteralExpression(feature.getAttributes().get("a"));
            int randomValue = ThreadLocalRandom.current().nextInt(2);
            if(randomValue == 0){
                a1 += (long)feature.getAttributes().get("a").getValue();
                AddExpression newDivExpression = new AddExpression(d1, literalExpression);
                d1 = new ParenthesisExpression(newDivExpression);
            }else{
                a1 *= (long)feature.getAttributes().get("a").getValue();
                MulExpression newDivExpression = new MulExpression(d1, literalExpression);
                d1 = new ParenthesisExpression(newDivExpression);
            }
        }

        for(int i=nd2+3;i<group.getFeatures().size();i++){
            Feature feature = group.getFeatures().get(i);
            LiteralExpression literalExpression = new LiteralExpression(feature.getAttributes().get("a"));
            int randomValue = ThreadLocalRandom.current().nextInt(2);
            if(randomValue == 0){
                AddExpression newDivExpression = new AddExpression(d2, literalExpression);
                d2 = new ParenthesisExpression(newDivExpression);
            }else{
                MulExpression newDivExpression = new MulExpression(d2, literalExpression);
                d2 = new ParenthesisExpression(newDivExpression);
            }
        }

        Random random = new Random();
        double randomD = (long) (random.nextDouble() * a1);
        Expression d = new NumberExpression(randomD);
        Constraint constraint = new GreaterEqualsEquationConstraint(
                new DivExpression(new ParenthesisExpression(d1),new ParenthesisExpression(d2))
                , d);
        featureModel.getOwnConstraints().add(constraint);
        return featureModel;
    }

    private FeatureModel generateDivModel(int n, int aMin, int aMax){
        FeatureModel featureModel = new FeatureModel();
        Feature rootFeature = new Feature("root_" + n);
        featureModel.setRootFeature(rootFeature);
        Group group = new Group(Group.GroupType.OPTIONAL);
        rootFeature.getChildren().add(group);

        int a1 = 1;
        for(int i=1;i<=n;i++){
            Feature feature = new Feature("feature" + i);
            var attributes = feature.getAttributes();
            Random random = new Random();
            int value = random.nextInt(aMax - aMin + 1) + aMin;
            if (i==1){
                a1 = value;
            }
            attributes.put("a", new Attribute<Long>("a", (long)value, feature));
            feature.setParentGroup(group);
            group.getFeatures().add(feature);
        }
        DivExpression div = new DivExpression(new LiteralExpression(group.getFeatures().get(0).getAttributes().get("a")), new LiteralExpression(group.getFeatures().get(1).getAttributes().get("a")));
        for(int i=2;i<group.getFeatures().size();i++){
            Feature feature = group.getFeatures().get(i);
            LiteralExpression literalExpression = new LiteralExpression(feature.getAttributes().get("a"));
            DivExpression newDivExpression = new DivExpression(div, literalExpression);
            div = newDivExpression;
        }

        Random random = new Random();
        double randomD = (long) (random.nextDouble() * a1);
        Expression d = new NumberExpression(randomD);
        Constraint constraint = new GreaterEqualsEquationConstraint(div, d);
        featureModel.getOwnConstraints().add(constraint);
        return featureModel;
    }

    private FeatureModel generateProductModel(int n, int aMin, int aMax){
        FeatureModel featureModel = new FeatureModel();
        Feature rootFeature = new Feature("root_" + n);
        featureModel.setRootFeature(rootFeature);
        Group group = new Group(Group.GroupType.OPTIONAL);
        rootFeature.getChildren().add(group);

        double valueProduct = 1;
        for(int i=1;i<=n;i++){
            Feature feature = new Feature("feature" + i);
            var attributes = feature.getAttributes();
            Random random = new Random();
            int value = random.nextInt(aMax - aMin + 1) + aMin + 1;
            valueProduct *= value;
            attributes.put("a", new Attribute<Long>("a", (long)value, feature));
            feature.setParentGroup(group);
            group.getFeatures().add(feature);
        }
        MulExpression product = new MulExpression(new LiteralExpression(group.getFeatures().get(0).getAttributes().get("a")), new LiteralExpression(group.getFeatures().get(1).getAttributes().get("a")));
        for(int i=2;i<group.getFeatures().size();i++){
            Feature feature = group.getFeatures().get(i);
            LiteralExpression literalExpression = new LiteralExpression(feature.getAttributes().get("a"));
            MulExpression newAddExpression = new MulExpression(literalExpression, product);
            product = newAddExpression;
        }

        Random random = new Random();
        long randomD = ThreadLocalRandom.current().nextLong(0, (long)valueProduct);
        //long randomD = random.nextInt((int)valueProduct + 1);
        Expression d = new NumberExpression(randomD);
        Constraint constraint = new GreaterEqualsEquationConstraint(product, d);
        featureModel.getOwnConstraints().add(constraint);
        return featureModel;
    }


    private FeatureModel generateAdditionModel(int n, int aMin, int aMax){
        FeatureModel featureModel = new FeatureModel();
        Feature rootFeature = new Feature("root_" + n);
        featureModel.setRootFeature(rootFeature);
        Group group = new Group(Group.GroupType.OPTIONAL);
        rootFeature.getChildren().add(group);

        int valueSum = 0;
        for(int i=1;i<=n;i++){
            Feature feature = new Feature("feature" + i);
            var attributes = feature.getAttributes();
            Random random = new Random();
            int value = random.nextInt(aMax - aMin + 1) + aMin;
            valueSum += value;
            attributes.put("a", new Attribute<Long>("a", (long)value, feature));
            feature.setParentGroup(group);
            group.getFeatures().add(feature);
        }
        AddExpression sum = new AddExpression(new LiteralExpression(group.getFeatures().get(0).getAttributes().get("a")), new LiteralExpression(group.getFeatures().get(1).getAttributes().get("a")));
        for(int i=2;i<group.getFeatures().size();i++){
            Feature feature = group.getFeatures().get(i);
            LiteralExpression literalExpression = new LiteralExpression(feature.getAttributes().get("a"));
            AddExpression newAddExpression = new AddExpression(literalExpression, sum);
            sum = newAddExpression;
        }

        Random random = new Random();
        long randomD = random.nextInt(valueSum + 1);
        Expression d = new NumberExpression(randomD);
        Constraint constraint = new GreaterEqualsEquationConstraint(sum, d);
        featureModel.getOwnConstraints().add(constraint);
        return featureModel;
    }

    private FeatureModel generateGroupCardinalityModel(int numberFeatures, int minCardinality, int maxCardinality) {
        FeatureModel featureModel = new FeatureModel();
        Feature rootFeature = new Feature("root_" + numberFeatures + "_" + minCardinality + "_" + maxCardinality);
        featureModel.setRootFeature(rootFeature);
        Group groupCardinality = new Group(Group.GroupType.GROUP_CARDINALITY);
        groupCardinality.setCardinality(new Cardinality(minCardinality, maxCardinality));
        rootFeature.getChildren().add(groupCardinality);
        groupCardinality.setParentFeature(rootFeature);
        for(int i=1;i<=numberFeatures;i++){
            Feature feature = new Feature("f_" + i);
            feature.setParentGroup(groupCardinality);
            groupCardinality.getFeatures().add(feature);
        }
        return featureModel;
    }

    private FeatureModel generateFeatureCardinalityModel(int minCardinality, int maxCardinality) {
        FeatureModel featureModel = new FeatureModel();
        Feature rootFeature = new Feature("root_" + minCardinality + "_" + maxCardinality);
        featureModel.setRootFeature(rootFeature);
        Group group = new Group(Group.GroupType.OPTIONAL);
        rootFeature.getChildren().add(group);
        group.setParentFeature(rootFeature);
        Feature feature = new Feature("f");
        Cardinality cardinality = new Cardinality(minCardinality, maxCardinality);
        feature.setCardinality(cardinality);
        feature.setParentGroup(group);
        feature.getChildren().addAll(getConstantSubtree());
        group.getFeatures().add(feature);

        return featureModel;
    }

    private List<Group> getConstantSubtree(){
        List<Group> groupList = new LinkedList<>();
        Group alternative = new Group(Group.GroupType.ALTERNATIVE);
        alternative.getFeatures().add(new Feature("firstAlternativeFeature"));
        alternative.getFeatures().add(new Feature("secondAlternativeFeature"));
        alternative.getFeatures().add(new Feature("thirdAlternativeFeature"));
        for(Feature f : alternative.getFeatures()){
            f.setParentGroup(alternative);
        }
        groupList.add(alternative);
        Group or = new Group(Group.GroupType.OR);
        or.getFeatures().add(new Feature("firstOrFeature"));
        or.getFeatures().add(new Feature("secondOrFeature"));
        or.getFeatures().add(new Feature("thirdOrFeature"));
        for(Feature f : or.getFeatures()){
            f.setParentGroup(or);
        }
        groupList.add(or);
        Group optional = new Group(Group.GroupType.OPTIONAL);
        optional.getFeatures().add(new Feature("firstOptionalFeature"));
        optional.getFeatures().add(new Feature("secondOptionalFeature"));
        optional.getFeatures().add(new Feature("thirdOptionalFeature"));
        for(Feature f : optional.getFeatures()){
            f.setParentGroup(optional);
        }
        groupList.add(optional);
        Group mandatory = new Group(Group.GroupType.MANDATORY);
        mandatory.getFeatures().add(new Feature("firstMandatoryFeature"));
        mandatory.getFeatures().add(new Feature("secondMandatoryFeature"));
        mandatory.getFeatures().add(new Feature("thirdMandatoryFeature"));
        for(Feature f : mandatory.getFeatures()){
            f.setParentGroup(mandatory);
        }
        groupList.add(mandatory);

        return groupList;
    }

    private void safeFeatureModelsToFiles(List<FeatureModel> featureModels, Path dirPath) throws IOException {
        for(FeatureModel featureModel : featureModels){
            String fileName = featureModel.getRootFeature().getFeatureName() + ".uvl";
            Path filePath = dirPath.resolve(fileName);
            safeFeatureModelToFile(featureModel, filePath);
        }
    }

    private void safeFeatureModelToFile(FeatureModel featureModel, Path filepath) throws IOException {
        Files.writeString(filepath, featureModel.toString(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }


}
