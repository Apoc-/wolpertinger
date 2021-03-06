/*
 * Copyright (c) Apoc- 2018
 *
 * File last modfied: 11.01.18 22:46
 */

package amber.parser;

import cherry.model.CodeUnit;
import cherry.model.CodeUnitDatumType;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.resolution.types.ResolvedType;
import amber.model.AnnotationModel;

public class AnnotationParser {
	AnnotationParser()  { }
	void setIdentifier(AnnotationModel model, AnnotationExpr anno) {
			String identifier = anno
					.asSingleMemberAnnotationExpr()
					.getMemberValue()
					.asStringLiteralExpr()
					.getValue();

			model.setIdentifier(identifier);
	}

	String getTypeName(ResolvedType rt) {
		String type;

		//todo if is array -> make non array: must be removed as array support is implemented
		if(rt.isReferenceType()) {
			type = rt.asReferenceType().getQualifiedName();
		} else if(rt.isArray()) {
			type = rt.describe();
			type = type.replace("[]", "");
		} else {
			type = rt.describe();
		}

		return type;
	}

	//todo maybe its just class ref?
	void addParentClassReference(CodeUnit codeUnit, String declaringClassName, String typeName) {
		if(declaringClassName.equals(typeName)) {
			codeUnit.addCodeUnitDatum(CodeUnitDatumType.PARENT_CLASS_REF, declaringClassName);
		}
	}
}
