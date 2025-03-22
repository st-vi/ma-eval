package org.example;

import de.vill.model.*;
import de.vill.model.constraint.Constraint;
import de.vill.model.constraint.GreaterEqualsEquationConstraint;
import de.vill.model.expression.*;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ModelGenerator {
    public static void main(String[] args) throws IOException {
        ModelGenerator modelGenerator = new ModelGenerator();


        List<FeatureModel> alternativeModels = modelGenerator.generateAlternativeModels(100, 8000, 100);
        Path modelDirAlternative = Paths.get("./eval/iso_models/alternative");
        modelGenerator.safeFeatureModelsToFiles(alternativeModels, modelDirAlternative);





/*

        var cardinalities = List.of(0.0, 0.25, 0.5, 0.75, 1.0);
        for(int minIndex=0;minIndex<cardinalities.size()-1;minIndex++){
            for(int maxIndex=minIndex+1;maxIndex<cardinalities.size();maxIndex++){
                String dirPath = "./eval/iso_models/groupCard/groupCard" + "_" + cardinalities.get(minIndex) + "_" + cardinalities.get(maxIndex);
                File directory = new File(dirPath);
                if (!directory.exists()) {
                    directory.mkdir();
                }
                List<FeatureModel> groupCardinalityModels = modelGenerator.generateGroupCardinalityModels(200, 5000, 100, cardinalities.get(minIndex), cardinalities.get(maxIndex));
                Path modelDirGroupCard = Paths.get(dirPath);
                modelGenerator.safeFeatureModelsToFiles(groupCardinalityModels, modelDirGroupCard);

            }
        }

 */



/*
        var feature_cardinalities = List.of(0.0, 0.25, 0.5, 0.75, 1.0);
        for(int minIndex=0;minIndex<feature_cardinalities.size()-1;minIndex++) {
            for (int maxIndex = minIndex + 1; maxIndex < feature_cardinalities.size(); maxIndex++) {
                String dirPath = "./eval/iso_models/featureCard/featureCard" + "_" + feature_cardinalities.get(minIndex) + "_" + feature_cardinalities.get(maxIndex);
                File directory = new File(dirPath);
                if (!directory.exists()) {
                    directory.mkdir();
                }
                List<FeatureModel> featureCardinalityModels = modelGenerator.generateFeatureCardinalityModels(200, 5000, 100,feature_cardinalities.get(minIndex), feature_cardinalities.get(maxIndex));
                Path modelDirFeatureCard = Paths.get(dirPath);
                modelGenerator.safeFeatureModelsToFiles(featureCardinalityModels, modelDirFeatureCard);
            }
        }

 */









/*
        for(int i=1;i<=9;i++){
            String dirPath = "./eval/iso_models/sum/sum" + "_" + i;
            File directory = new File(dirPath);
            if (!directory.exists()) {
                directory.mkdir();
            }

            List<FeatureModel> sumModels = modelGenerator.generateSumModels(2, 100, 1, 1, 100, i/10.0);
            Path modelDirSum = Paths.get(dirPath);
            modelGenerator.safeFeatureModelsToFiles(sumModels, modelDirSum);
        }

 */










/*
        for(int i=1;i<=9;i++) {
            String dirPath = "./eval/iso_models/product/product" + "_" + i;
            File directory = new File(dirPath);
            if (!directory.exists()) {
                directory.mkdir();
            }
            List<FeatureModel> productModels = modelGenerator.generateProductModels(2, 18, 1, 1, 10, i/10.0);
            Path modelDirProduct = Paths.get(dirPath);
            modelGenerator.safeFeatureModelsToFiles(productModels, modelDirProduct);
        }


 */








        /*

        List<FeatureModel> divModels = modelGenerator.generateDivModels(15, 30, 5, 0, 100);
        Path modelDirDiv = Paths.get("./eval/iso_models/div");
        modelGenerator.safeFeatureModelsToFiles(divModels, modelDirDiv);

         */


        for(int i=5;i<=5;i++) {
            String dirPath = "./eval/iso_models/div/div" + "_" + i;
            File directory = new File(dirPath);
            if (!directory.exists()) {
                directory.mkdir();
            }
            List<FeatureModel> divModels2 = modelGenerator.generateDivModels2(6, 8, 2, 1, 10, i/10.0);
            Path modelDirDiv2 = Paths.get(dirPath);
            modelGenerator.safeFeatureModelsToFiles(divModels2, modelDirDiv2);

        }
















        //FeatureModel test = modelGenerator.generateDivModel2(10, 0, 100);
        //System.out.println(test.toString());
    }

    private List<FeatureModel> generateDivModels2(int minN, int maxN, int stepSize, int aMin, int aMax, double complexity) {
        List<FeatureModel> generatedModelList = new LinkedList<>();
        for(int n=minN;n<=maxN;n+=stepSize){
            generatedModelList.add(generateDivModel2(n,aMin,aMax, complexity));
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

    private List<FeatureModel> generateProductModels(int minN, int maxN, int stepSize, int aMin, int aMax, double complexity) {
        List<FeatureModel> generatedModelList = new LinkedList<>();
        for(int n=minN;n<=maxN;n+=stepSize){
            generatedModelList.add(generateProductModel(n,aMin,aMax, complexity));
        }
        return generatedModelList;
    }

    private List<FeatureModel> generateSumModels(int minN, int maxN, int stepSize, int aMin, int aMax, double complexity) {
        List<FeatureModel> generatedModelList = new LinkedList<>();
        for(int n=minN;n<=maxN;n+=stepSize){
            generatedModelList.add(generateAdditionModel(n,aMin,aMax, complexity));
        }
        return generatedModelList;
    }

    private List<FeatureModel> generateGroupCardinalityModels(int minNumberChildren, int maxNumberChildren, int stepSize, double minCard, double maxCard) {
        List<FeatureModel> generatedModelList = new LinkedList<>();
        for (int numberChildren = minNumberChildren;numberChildren<=maxNumberChildren;numberChildren+=stepSize){
            generatedModelList.add(generateGroupCardinalityModel(numberChildren, minCard == 0.0 ? 1 : (int) Math.ceil(numberChildren * minCard), (int) Math.ceil(numberChildren * maxCard)));
        }
        return generatedModelList;
    }

    private List<FeatureModel> generateFeatureCardinalityModels(int minN, int maxN, int stepSize, double minCard, double maxCard) {
        List<FeatureModel> generatedModelList = new LinkedList<>();
        for (int numberChildren = minN;numberChildren<=maxN;numberChildren+=stepSize){
            generatedModelList.add(generateFeatureCardinalityModel(minCard == 0 ? 1 : (int) Math.ceil(numberChildren * minCard), (int) Math.ceil(numberChildren * maxCard), numberChildren));
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

    private FeatureModel generateDivModel2(int n, int aMin, int aMax, double complexity){
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
        Expression d1 = new AddExpression(new LiteralExpression(group.getFeatures().get(0).getAttributes().get("a")), new LiteralExpression(group.getFeatures().get(1).getAttributes().get("a")));
        Expression d2 = new MulExpression(new LiteralExpression(group.getFeatures().get(nd2+1).getAttributes().get("a")), new LiteralExpression(group.getFeatures().get(nd2+2).getAttributes().get("a")));

        long a1 = 1;
        for(int i=2;i<=nd2;i++){
            Feature feature = group.getFeatures().get(i);
            LiteralExpression literalExpression = new LiteralExpression(feature.getAttributes().get("a"));
            int randomValue = ThreadLocalRandom.current().nextInt(2);
            if(randomValue == 0){
                a1 += (long)feature.getAttributes().get("a").getValue();
                AddExpression newDivExpression = new AddExpression(d1, literalExpression);
                d1 =newDivExpression;
            }else{
                a1 *= (long)feature.getAttributes().get("a").getValue();
                MulExpression newDivExpression = new MulExpression(d1, literalExpression);
                d1 = newDivExpression;
            }
        }

        for(int i=nd2+3;i<group.getFeatures().size();i++){
            Feature feature = group.getFeatures().get(i);
            LiteralExpression literalExpression = new LiteralExpression(feature.getAttributes().get("a"));
            int randomValue = ThreadLocalRandom.current().nextInt(2);
            if(randomValue == 0){
                AddExpression newDivExpression = new AddExpression(d2, literalExpression);
                d2 = newDivExpression;
            }else{
                MulExpression newDivExpression = new MulExpression(d2, literalExpression);
                d2 = newDivExpression;
            }
        }

        final int runs = 10000;
        var a1_results = new LinkedList<Long>();
        for (int i=1;i<=runs;i++){
            a1_results.add(evaluateAtRandom(d1, complexity));
        }
        var average_d1 = getMedian(a1_results);
        var a2_results = new LinkedList<Long>();
        for (int i=1;i<=runs;i++){
            long tmp_res = (long) evaluateAtRandom(d2, complexity);
            if (tmp_res != 0){
                a2_results.add(tmp_res);
            }
        }
        var average_d2 = !a2_results.isEmpty() ? getMedian(a2_results) : 0.0;
        Expression d = new NumberExpression(Math.round((((double) average_d1 / (double) average_d2)) * 1000.0) / 1000.0);
        Constraint constraint = new GreaterEqualsEquationConstraint(
                new DivExpression(new ParenthesisExpression(d1),new ParenthesisExpression(d2))
                , d);
        featureModel.getOwnConstraints().add(constraint);
        return featureModel;
    }

    private long evaluateAtRandom(Expression expression, double complexity) {
        if (expression instanceof AddExpression){
            return evaluateAtRandom(((AddExpression) expression).getLeft(), complexity) + evaluateAtRandom(((AddExpression) expression).getRight(), complexity);
        }else if(expression instanceof MulExpression){
            return evaluateAtRandom(((MulExpression) expression).getLeft(), complexity) * evaluateAtRandom(((MulExpression) expression).getRight(), complexity);
        }else if(expression instanceof ParenthesisExpression){
            return evaluateAtRandom(((ParenthesisExpression) expression).getContent(), complexity);
        }else if(expression instanceof NumberExpression){
            return (long) ((NumberExpression) expression).getNumber();
        }else{
            var literalExpression = (LiteralExpression) expression;
            Random rand = new Random();
            int randomBit = generateWithProbability(complexity);
            if (randomBit == 0){
                return 0;
            }else{
                Attribute<?> attribute = (Attribute<?>) literalExpression.getContent();
                final Object attributeValue = attribute.getValue();
                return (long) attributeValue;
            }
        }
    }

    public static int generateWithProbability(double probability) {
        if (probability < 0.0 || probability > 1.0) {
            throw new IllegalArgumentException("Probability must be between 0 and 1");
        }
        Random random = new Random();
        return random.nextDouble() < probability ? 1 : 0;
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

    private FeatureModel generateProductModel(int n, int aMin, int aMax, double complexity){
        FeatureModel featureModel = new FeatureModel();
        Feature rootFeature = new Feature("root_" + n);
        featureModel.setRootFeature(rootFeature);
        Group group = new Group(Group.GroupType.OPTIONAL);
        rootFeature.getChildren().add(group);

        long valueProduct = 1;
        for(int i=1;i<=n;i++){
            Feature feature = new Feature("feature" + i);
            var attributes = feature.getAttributes();
            Random random = new Random();
            int value = random.nextInt(aMax - aMin + 1) + aMin;
            valueProduct *= (value + 1);
            attributes.put("a", new Attribute<Long>("a", (long)value, feature));
            feature.setParentGroup(group);
            group.getFeatures().add(feature);
        }
        MulExpression product = new MulExpression(
                new ParenthesisExpression(
                        new AddExpression(
                                new NumberExpression(1.0),
                                new LiteralExpression(group.getFeatures().get(0).getAttributes().get("a"))
                                )
                ),
                new ParenthesisExpression(
                        new AddExpression(
                                new NumberExpression(1.0),
                                new LiteralExpression(group.getFeatures().get(1).getAttributes().get("a"))
                        )
                )
        );
        for(int i=2;i<group.getFeatures().size();i++){
            Feature feature = group.getFeatures().get(i);
            LiteralExpression literalExpression = new LiteralExpression(feature.getAttributes().get("a"));
            MulExpression newAddExpression = new MulExpression(
                    new ParenthesisExpression(
                            new AddExpression(
                                    new NumberExpression(1.0),
                                    literalExpression
                            )
                    ),
                    product);
            product = newAddExpression;
        }

        //Random random = new Random();
        //long randomD = ThreadLocalRandom.current().nextLong(0, (long)valueProduct);
        //long randomD = random.nextInt((int)valueProduct + 1);
        final int runs = 10000;
        var p_list = new LinkedList<Long>();
        for (int i=1;i<=runs;i++){
            var tmp = evaluateAtRandom(product, complexity);
            p_list.add(tmp);
        }
        var average_d1 = getMedian(p_list);
        Expression d = new LongNumberExpression(average_d1);
        Constraint constraint = new GreaterEqualsEquationConstraint(product, d);
        featureModel.getOwnConstraints().add(constraint);
        return featureModel;
    }

    public static long getMedian(List<Long> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("The list must not be null or empty");
        }

        // Sort the list
        Collections.sort(values);

        int size = values.size();
        if (size % 2 == 1) {
            // Odd size: Return the middle value
            return values.get(size / 2);
        } else {
            // Even size: Return the average of the two middle values
            long middle1 = values.get(size / 2 - 1);
            long middle2 = values.get(size / 2);
            return (middle1 + middle2) / 2; // Use 2.0 to get a double result
        }
    }


    private FeatureModel generateAdditionModel(int n, int aMin, int aMax, double complexity){
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

        Expression d = new NumberExpression((double)valueSum * complexity);
        Constraint constraint = new GreaterEqualsEquationConstraint(sum, d);
        featureModel.getOwnConstraints().add(constraint);
        return featureModel;
    }

    private FeatureModel generateGroupCardinalityModel(int numberFeatures, int minCardinality, int maxCardinality) {
        FeatureModel featureModel = new FeatureModel();
        Feature rootFeature = new Feature("root_" + numberFeatures);
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

    private FeatureModel generateFeatureCardinalityModel(int minCardinality, int maxCardinality, int numberChildren) {
        FeatureModel featureModel = new FeatureModel();
        Feature rootFeature = new Feature("root_" + numberChildren);
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
