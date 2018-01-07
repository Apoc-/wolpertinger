/*
 * Copyright (c) Apoc- 2017
 *
 * File last modfied: 26.12.17 00:07
 */

package sphynx.unitmodel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

//Todo this structure needs reevaluation for better transformability and
//I HATE PRIMITIVE DATATYPES
public class CodeUnitDatum<T> implements Serializable {
	private T datumData;

	CodeUnitDatum(T datumData) {
		this.datumData = datumData;
	}

	@Override
	public String toString() {
		if(datumData.getClass().isArray())
			return Arrays.toString((Object[]) datumData); //smelly smell is smelly

		return datumData.toString();
	}
}