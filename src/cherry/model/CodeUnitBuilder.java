/*
 * Copyright (c) Apoc- 2018
 *
 * File last modfied: 11.01.18 23:09
 */
package cherry.model;

public class CodeUnitBuilder {
	private final CodeUnit codeUnit;

	private CodeUnitBuilder() {
		this.codeUnit = new CodeUnit(CodeUnitType.CLASS);
	}

	public static CodeUnitBuilder createWithIdentifier(String identifier) {
		CodeUnitBuilder cb = new CodeUnitBuilder();
		cb.codeUnit.addCodeUnitDatum(CodeUnitDatumType.IDENTIFIER, identifier);
		return cb;
	}

	public CodeUnitBuilder setCodeUnityType(CodeUnitType type) {
		this.codeUnit.setType(type);
		return this;
	}

	public CodeUnitBuilder withModifiers(CodeUnitModifier... modifiers) {
		this.codeUnit.addCodeUnitDatum(CodeUnitDatumType.MODIFIER, modifiers);
		return this;
	}

	public CodeUnitBuilder withDataType(Class dataType) {
		this.codeUnit.addCodeUnitDatum(CodeUnitDatumType.DATA_TYPE,dataType);
		return this;
	}

	public CodeUnitBuilder withSubCodeUnit(CodeUnit codeUnit) {
		this.codeUnit.addSubCodeUnit(codeUnit);
		return this;
	}

	public CodeUnit end() {
		return codeUnit;
	}
}