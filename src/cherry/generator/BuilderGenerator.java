/*
 * Copyright (c) Apoc- 2018
 *
 * File last modfied: 05.01.18 22:05
 */

package cherry.generator;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.squareup.javapoet.*;
import org.apache.commons.lang3.SerializationUtils;
import amber.model.AnnotationModel;
import cherry.model.CodeUnit;
import amber.parser.visitor.JavaClassAnnotationVisitor;
import amber.parser.visitor.JavaFieldAnnotationVisitor;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BuilderGenerator {
	private static BuilderGenerator instance;
	private final List<AnnotationModel> models;

	public static BuilderGenerator getInstance() {
		if(instance == null)
			instance = new BuilderGenerator();
		return instance;
	}

	private BuilderGenerator() {
		models = new ArrayList<>();
	}

	public void generateUnitBuilders(String sourcePath, String targetPath, String targetPackage) {
		CompilationUnit cu = parseCodeFile(sourcePath);
		populateUnitBuilderModels(cu);

		models.forEach(m -> generateUnitBuilder(m, targetPath, targetPackage));
	}

	private CompilationUnit parseCodeFile(String path) {
		File codeFile = new File(path);
		CompilationUnit cu = null;

		try {
			cu = JavaParser.parse(codeFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return cu;
	}

	private void populateUnitBuilderModels(CompilationUnit cu) {
		//visit classes
		new JavaClassAnnotationVisitor().visit(cu, this.models);

		//visit fields
		new JavaFieldAnnotationVisitor().visit(cu, this.models);
	}


	//Todo: Move MethodSpec creations to factory
	private void generateUnitBuilder(AnnotationModel model, String targetPath, String targetPackage) {
		String builderClassIdentifier = model.getIdentifier() + "UnitBuilder";
		TypeName builderClassName = ClassName.get(targetPackage, builderClassIdentifier);

		MethodSpec constructor = MethodSpec.constructorBuilder()
				.addModifiers(Modifier.PRIVATE)
				.addStatement("initializeDefaultCodeUnit()")
				.build();

		MethodSpec codeUnitInitializer = generateInitDefCodeUnitMethod(model.getDefaultCodeUnit());

		MethodSpec builderInitializer = BuilderMethodFactory.createForType(BuilderMethodType.CREATE_WITH_IDENTIFIER, builderClassName);
		MethodSpec builderFinalizer = BuilderMethodFactory.createForType(BuilderMethodType.END, builderClassName);

		List<MethodSpec> methods = model.getAnnotationData()
				.stream()
				.map(anno -> BuilderMethodFactory
						.createForType(BuilderMethodMapper.getBuilderMethodType(anno), builderClassName))
				.collect(Collectors.toList());

		TypeSpec builderType = TypeSpec
				.classBuilder(builderClassIdentifier)
				.addModifiers(Modifier.PUBLIC)
				.addField(CodeUnit.class, "codeUnit", Modifier.PRIVATE)
				.addMethod(constructor)
				.addMethod(codeUnitInitializer)
				.addMethod(builderInitializer)
				.addMethod(builderFinalizer)
				.addMethods(methods)
				.build();

		JavaFile javaFile = JavaFile.builder(targetPackage, builderType)
				.build();

		try {
			File targetFile = new File(targetPath);
			javaFile.writeTo(targetFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Generated " + builderClassIdentifier + " in " + targetPath + " with package " + targetPackage);
	}

	//Todo: Find better readable solutions
	private MethodSpec generateInitDefCodeUnitMethod(CodeUnit sourceCodeUnit) {
		byte[] serializedCodeUnit = SerializationUtils.serialize(sourceCodeUnit);

		String codeUnitArrayLiteral = Arrays
				.toString(serializedCodeUnit)
				.replace("[","{")
				.replace("]","}");

		return MethodSpec.methodBuilder("initializeDefaultCodeUnit")
				.addComment("Initializes this builder's data with default data encoded into a byte[]")
				.addModifiers(Modifier.PRIVATE)
				.addStatement("byte[] serializedCodeUnit = new byte[] $L", codeUnitArrayLiteral)
				.addStatement("this.codeUnit = $T.deserialize(serializedCodeUnit)", SerializationUtils.class)
				.build();
	}
}