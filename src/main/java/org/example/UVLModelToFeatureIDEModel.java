package org.example;

import de.ovgu.featureide.fm.core.ExtensionManager;
import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.impl.*;
import de.ovgu.featureide.fm.core.io.manager.FileHandler;
import de.ovgu.featureide.fm.core.io.uvl.UVLFeatureModelFormat;
import de.vill.model.*;
import de.vill.model.Feature;
import de.vill.model.FeatureModel;
import de.vill.model.constraint.*;
import de.vill.model.constraint.Constraint;
import org.prop4j.Node;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static de.ovgu.featureide.fm.core.io.uvl.UVLFeatureModelFormat.FILE_EXTENSION;

public class UVLModelToFeatureIDEModel {
    public UVLModelToFeatureIDEModel(FeatureModel uvlModel){
        this.uvlModel = uvlModel;
    }
    public MultiFeatureModelFactory factory;
    public FeatureModel uvlModel;

    public MultiFeatureModel constructFeatureModel(Path uvlPath) throws ExtensionManager.NoSuchExtensionException {
        final FileHandler<IFeatureModel> fileHandler = new FileHandler<>(uvlPath, null, null);
        final UVLFeatureModelFormat format = new UVLFeatureModelFormat();
        final MultiFeatureModel fm = (MultiFeatureModel) FMFactoryManager.getInstance().getFactory(uvlPath, format).create();
        fileHandler.setObject(fm);
        fileHandler.setFormat(format);
        factory = (MultiFeatureModelFactory) FMFactoryManager.getInstance().getFactory(fm);
        fm.reset();
        parseImports(uvlModel, fm);
        final IFeature rootFeature;
        final Feature uvlRootFeature = uvlModel.getRootFeature();
        rootFeature = parseFeature(fm, uvlRootFeature, null);
        fm.getStructure().setRoot(rootFeature.getStructure());
        fm.addAttribute(rootFeature.getName(), "NS_ATTRIBUTE_FEATURE", uvlModel.getNamespace());
        parseConstraints(fm);
        return fm;
    }

    private IFeature parseFeature(MultiFeatureModel fm, Feature uvlFeature, IFeature parentFeature) {
        final MultiFeature feature = factory.createFeature(fm, uvlFeature.getReferenceFromSpecificSubmodel(""));
        fm.addFeature(feature);

        final Attribute<?> featureDescription = uvlFeature.getAttributes().get("FEATURE_DESCRIPTION_ATTRIBUTE_NAME");
        if ((featureDescription != null) && (featureDescription.getValue() instanceof String)) {
            feature.getProperty().setDescription(((String) featureDescription.getValue()).replace("\\n", "\n"));
        }

        if (parentFeature != null) {
            parentFeature.getStructure().addChild(feature.getStructure());
        }
        if (uvlFeature.getAttributes().containsKey("abstract")) {
            feature.getStructure().setAbstract(true);
        }
        parseAttributes(fm, feature, uvlFeature);
        if (uvlFeature.getReferenceFromSpecificSubmodel("").contains(".")) {
            feature.setType(MultiFeature.TYPE_INTERFACE);
        }
        if (uvlFeature.isSubmodelRoot()) {
            fm.addAttribute(uvlFeature.getReferenceFromSpecificSubmodel(""), "NS_ATTRIBUTE_FEATURE", uvlFeature.getRelatedImport().getNamespace());
        }

        if (uvlFeature.getChildren().size() == 1) {
            parseGroup(fm, uvlFeature.getChildren().get(0), feature);
        } else {
            feature.getStructure().setAnd();
            for (final Group group : uvlFeature.getChildren()) {
                final MultiFeature groupParent = factory.createFeature(fm, getGroupFeatureName(fm, feature, group));
                fm.addFeature(groupParent);
                groupParent.getStructure().setAbstract(true);
                groupParent.getStructure().setMandatory(true);
                feature.getStructure().addChild(groupParent.getStructure());
                parseGroup(fm, group, groupParent);
            }
        }

        return feature;
    }

    private String getGroupFeatureName(IFeatureModel featureModel, IFeature parentFeature, Group group) {
        int index = 0;
        String name = parentFeature.getName() + "_" + group.GROUPTYPE.toString() + "_" + index;
        while (featureModel.getFeature(name) != null) {
            name = parentFeature.getName() + "_" + group.GROUPTYPE.toString() + "_" + ++index;
        }
        return name;
    }

    private void parseGroup(MultiFeatureModel fm, Group uvlGroup, IFeature parentFeature) {
        final List<IFeature> children = new LinkedList<>();
        for (final Feature feature : uvlGroup.getFeatures()) {
            children.add(parseFeature(fm, feature, parentFeature));
        }

        if (uvlGroup.GROUPTYPE.equals(Group.GroupType.GROUP_CARDINALITY)) {
            if ((uvlGroup.getCardinality().lower == 1) && (uvlGroup.getCardinality().upper == uvlGroup.getFeatures().size())) {
                parentFeature.getStructure().setOr();
            } else if ((uvlGroup.getCardinality().lower == 1) && (uvlGroup.getCardinality().upper == 1)) {
                parentFeature.getStructure().setAlternative();
            } else if ((uvlGroup.getCardinality().lower == 0) && (uvlGroup.getCardinality().upper == uvlGroup.getFeatures().size())) {
                // optional is true if nothing else is set
            } else if ((uvlGroup.getCardinality().lower == uvlGroup.getCardinality().upper )
                    && (uvlGroup.getCardinality().upper == uvlGroup.getFeatures().size())) {
                children.forEach(f -> f.getStructure().setMandatory(true));
            }
        }

        switch (uvlGroup.GROUPTYPE) {
            case OR:
                parentFeature.getStructure().setOr();
                break;
            case ALTERNATIVE:
                parentFeature.getStructure().setAlternative();
                break;
            case OPTIONAL:
                break;
            case MANDATORY:
                children.forEach(f -> f.getStructure().setMandatory(true));
                break;
            case GROUP_CARDINALITY:
                break;
            default:
                break;
        }
    }

    private void parseAttributes(MultiFeatureModel fm, MultiFeature feature, Feature uvlFeature) {
        uvlFeature.getAttributes().entrySet().stream().forEachOrdered(e -> parseAttribute(fm, feature, e.getKey(), e.getValue().getValue()));
    }

    protected void parseAttribute(MultiFeatureModel fm, MultiFeature feature, String attributeKey, Object attributeValue) {
        if (attributeValue instanceof Constraint) {
            parseConstraint(fm, (Constraint) attributeValue);
        }

    }

    private void parseConstraints(MultiFeatureModel fm) {
        final List<Constraint> ownConstraints = uvlModel.getOwnConstraints();
        final List<Constraint> allConstraints = new LinkedList<>();
        for (final Import importLine : uvlModel.getImports()) {
            // only consider submodels from imports that are actually used
            if (importLine.isReferenced()) {
                allConstraints.addAll(importLine.getFeatureModel().getConstraints());
            }
        }

        for (final Constraint constraint : ownConstraints) {
            parseOwnConstraint(fm, constraint);
        }

        for (final Constraint constraint : allConstraints) {
            parseConstraint(fm, constraint);
        }

    }

    private void parseConstraint(MultiFeatureModel fm, Constraint c) {
        parseConstraint(fm, c, false);
    }

    private void parseOwnConstraint(MultiFeatureModel fm, Constraint c) {
        parseConstraint(fm, c, true);
    }

    private void parseConstraint(MultiFeatureModel fm, Constraint c, boolean own) {
        try {
            final Node constraint = parseConstraint(c);
            if (constraint != null) {
                final MultiConstraint newConstraint = factory.createConstraint(fm, constraint);
                if (own) {
                    fm.addOwnConstraint(newConstraint);
                } else {
                    newConstraint.setType(MultiFeature.TYPE_INTERFACE);
                    fm.addConstraint(newConstraint);
                }
            }
        } catch (final RuntimeException e) {
            // Contained invalid reference. Already added to problem list
        }
    }

    private Node parseConstraint(Constraint constraint) {
        if (constraint instanceof AndConstraint) {
            return new org.prop4j.And(parseConstraint(((AndConstraint) constraint).getLeft()), parseConstraint(((AndConstraint) constraint).getRight()));
        } else if (constraint instanceof EquivalenceConstraint) {
            return new org.prop4j.Equals(parseConstraint(((EquivalenceConstraint) constraint).getLeft()),
                    parseConstraint(((EquivalenceConstraint) constraint).getRight()));
        } else if (constraint instanceof ImplicationConstraint) {
            return new org.prop4j.Implies(parseConstraint(((ImplicationConstraint) constraint).getLeft()),
                    parseConstraint(((ImplicationConstraint) constraint).getRight()));
        } else if (constraint instanceof NotConstraint) {
            return new org.prop4j.Not(parseConstraint(((NotConstraint) constraint).getContent()));
        } else if (constraint instanceof OrConstraint) {
            return new org.prop4j.Or(parseConstraint(((OrConstraint) constraint).getLeft()), parseConstraint(((OrConstraint) constraint).getRight()));
        } else if (constraint instanceof ParenthesisConstraint) {
            return parseConstraint(((ParenthesisConstraint) constraint).getContent());
        } else if (constraint instanceof LiteralConstraint) {
            return new org.prop4j.Literal(((LiteralConstraint) constraint).toString(false, "").replace("\"", ""));
        } else if (constraint instanceof MultiOrConstraint){
            Object[] childConstraints = new Object[constraint.getConstraintSubParts().size()];
            for(int i=0;i< childConstraints.length;i++){
                childConstraints[i] = parseConstraint(constraint.getConstraintSubParts().get(i));
            }
            return  new org.prop4j.Or(childConstraints);
        }
        else {
            return null;
        }
    }

    private void parseImports(FeatureModel uvlModel, MultiFeatureModel fm) {
        final List<Import> imports = uvlModel.getImports();
        for (final Import importLine : imports) {
            // only consider submodels that are actually referenced somewhere
            if (importLine.isReferenced()) {
                parseImports(importLine.getFeatureModel(), fm);
                parseImport(fm, importLine);
            }
        }
    }

    private void parseImport(MultiFeatureModel fm, Import i) {
        final Path path = fm.getSourceFile().resolveSibling(i.getNamespace().replace(".", "/") + "." + FILE_EXTENSION);
        fm.addInstance(i.getNamespace(), i.getAlias(), path);
    }
}
