package org.example;

import de.vill.model.Feature;
import de.vill.model.FeatureModel;
import de.vill.model.Group;

public class FMStatistics {
    public final FeatureModel featureModel;
    private int orCounter = 0;
    private int alternativeCounter = 0;
    private int optionalCounter = 0;
    private int mandatoryCounter = 0;
    private int totalGroupCounter = 0;
    private int featureCounter = 0;
    private int constraintCounter = 0;

    public FMStatistics(FeatureModel featureModel) {
        this.featureModel = featureModel;
        constraintCounter = featureModel.getOwnConstraints().size();
        traverseModel(featureModel.getRootFeature());
    }


    private void traverseModel(Feature feature) {
        featureCounter++;
        for (Group group : feature.getChildren()){
            traverseModel(group);
        }
    }

    private void traverseModel(Group group) {
        totalGroupCounter++;
        if (group.GROUPTYPE == Group.GroupType.OPTIONAL){
            optionalCounter++;
        }else if(group.GROUPTYPE == Group.GroupType.MANDATORY){
            mandatoryCounter++;
        }else if(group.GROUPTYPE == Group.GroupType.ALTERNATIVE){
            alternativeCounter++;
        }else if(group.GROUPTYPE == Group.GroupType.OR) {
            orCounter++;
        }
        for (Feature feature : group.getFeatures()){
            traverseModel(feature);
        }
    }

    public void printStatistic(){
        System.out.println((double) optionalCounter/(double)totalGroupCounter + ";" + (double) mandatoryCounter/(double)totalGroupCounter + ";" + (double) orCounter/(double)totalGroupCounter + ";" + (double) alternativeCounter/(double)totalGroupCounter + ";" + (double)constraintCounter/(double) featureCounter);
    }
}
