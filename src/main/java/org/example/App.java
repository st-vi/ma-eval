package org.example;

import de.ovgu.featureide.fm.core.ExtensionManager;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.impl.FMFactoryManager;
import de.ovgu.featureide.fm.core.base.impl.FMFormatManager;
import de.ovgu.featureide.fm.core.init.FMCoreLibrary;
import de.ovgu.featureide.fm.core.init.LibraryManager;
import de.ovgu.featureide.fm.core.io.dimacs.DIMACSFormat;
import de.ovgu.featureide.fm.core.io.manager.FileHandler;
import de.ovgu.featureide.fm.core.io.uvl.UVLFeatureModelFormat;
import de.vill.main.UVLModelFactory;
import de.vill.model.FeatureModel;
import de.vill.model.LanguageLevel;
import de.vill.model.constraint.Constraint;
import de.vill.util.ConvertFeatureCardinalityForOPB;
import de.vill.util.FeatureModelEncoding;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Hello world!
 *
 */
public class App 
{
    public static boolean tseitin = false;

    public static void main( String[] args ) throws IOException {
        final File UVL_FILE = new File(args[0]);
        final File TARGET_FILE = new File(args[1]);
        final Target target = args[2].equals("dimacs") ? Target.DIMACS : Target.OPB;
        tseitin = args.length > 3 && args[3].equals("tseitin");

        switch (target) {
            case DIMACS: {
                uvlToDimacsFeatureIDE(UVL_FILE, TARGET_FILE);
                break;
            }
            case OPB: {
                uvlToOPB(UVL_FILE, TARGET_FILE);
                break;
            }
        }
    }

    public static enum Target{
        DIMACS,
        OPB
    }

    public static void uvlToOPB(File modelFile, File targetFile) throws IOException {
        UVLModelFactory uvlModelFactory = new UVLModelFactory();
        FeatureModel featureModel = loadUVLFeatureModelFromFile(modelFile.toString());
        ConvertFeatureCardinalityForOPB convertFeatureCardinalityForOPB = new ConvertFeatureCardinalityForOPB();
        convertFeatureCardinalityForOPB.convertFeatureModel(featureModel);

        Set<LanguageLevel> levels = new HashSet<>();
        levels.add(LanguageLevel.AGGREGATE_FUNCTION);
        levels.add( LanguageLevel.STRING_CONSTRAINTS);
        uvlModelFactory.convertLanguageLevel(featureModel, levels);

        List<Constraint> constraintList = new LinkedList<>();
        for (Constraint constraint : featureModel.getOwnConstraints()){
            constraintList.add(de.vill.util.ReplaceClasses.replace(constraint));
        }
        featureModel.getOwnConstraints().clear();
        featureModel.getOwnConstraints().addAll(constraintList);


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile))) {
            if (tseitin) {
                writer.append(FeatureModelEncoding.toOPBString(featureModel));
            }else{
                FeatureModelEncoding.writeOPBStringToFile(featureModel, modelFile, targetFile, writer);
            }
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public static void uvlToDimacsFeatureIDE(File modelFile, File targetFile) throws IOException {
        UVLModelFactory uvlModelFactory = new UVLModelFactory();
        FeatureModel featureModel = loadUVLFeatureModelFromFile(modelFile.toString());
        Set<LanguageLevel> levels = new HashSet<>();
        levels.add(LanguageLevel.BOOLEAN_LEVEL);
        levels.add(LanguageLevel.TYPE_LEVEL);
        uvlModelFactory.convertExceptAcceptedLanguageLevel(featureModel, levels);

        LibraryManager.registerLibrary(FMCoreLibrary.getInstance());
        FMFormatManager.getInstance().addExtension(new UVLFeatureModelFormat());

        IFeatureModel fm = getFeatureIdeFMFromString(modelFile.toPath(), featureModel.toString());
        FileHandler.save(targetFile.toPath(), fm, new DIMACSFormat());
    }

    public static IFeatureModel getFeatureIdeFMFromString(Path path, String content) throws IOException {
        final FileHandler<IFeatureModel> fileHandler = new FileHandler<>(path, null, null);
        final UVLFeatureModelFormat format = new UVLFeatureModelFormat();
        try {
            final IFeatureModel fm = FMFactoryManager.getInstance().getFactory(path, format).create();
            fileHandler.setObject(fm);
            fileHandler.setFormat(format);
            format.getInstance().read(fm, content, path);

        }catch (ExtensionManager.NoSuchExtensionException e) {
            throw new IOException("Error while parsing UVL model");
        }
        return fileHandler.getObject();
    }

    private static FeatureModel loadUVLFeatureModelFromFile(String path) throws IOException {
        Path filePath = Paths.get(path);
        String content = new String(Files.readAllBytes(filePath));
        UVLModelFactory uvlModelFactory = new UVLModelFactory();
        FeatureModel featureModel = uvlModelFactory.parse(content);
        return featureModel;
    }
}
