/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.univocity.parsers.common.processor;

import java.util.*;

import com.univocity.parsers.common.*;

public abstract class BatchedColumnProcessor implements RowProcessor, ColumnReaderProcessor<String> {

	private final ColumnSplitter<String> splitter;
	private final int rowsPerBatch;
	private int batchCount;
	private int batchesProcessed;

	public BatchedColumnProcessor(int rowsPerBatch) {
		splitter = new ColumnSplitter<String>(rowsPerBatch);
		this.rowsPerBatch = rowsPerBatch;
	}

	@Override
	public final void processStarted(ParsingContext context) {
		splitter.reset();
		batchCount = 0;
		batchesProcessed = 0;
	}

	@Override
	public final void rowProcessed(String[] row, ParsingContext context) {
		splitter.addValuesToColumns(row, context);
		batchCount++;

		if (batchCount >= rowsPerBatch) {
			batchProcessed(batchCount);
			batchCount = 0;
			splitter.clearValues();
			batchesProcessed++;
		}
	}

	@Override
	public void processEnded(ParsingContext context) {
		if (batchCount > 0) {
			batchProcessed(batchCount);
		}
	}

	@Override
	public final String[] getHeaders() {
		return splitter.getHeaders();
	}

	@Override
	public final List<List<String>> getColumnValuesAsList() {
		return splitter.getColumnValues();
	}

	@Override
	public final void putColumnValuesInMapOfNames(Map<String, List<String>> map) {
		splitter.putColumnValuesInMapOfNames(map);
	}

	@Override
	public final void putColumnValuesInMapOfIndexes(Map<Integer, List<String>> map) {
		splitter.putColumnValuesInMapOfIndexes(map);
	}

	@Override
	public final Map<String, List<String>> getColumnValuesAsMapOfNames() {
		return splitter.getColumnValuesAsMapOfNames();
	}

	@Override
	public final Map<Integer, List<String>> getColumnValuesAsMapOfIndexes() {
		return splitter.getColumnValuesAsMapOfIndexes();
	}

	public int getRowsPerBatch() {
		return rowsPerBatch;
	}

	public int getBatchesProcessed() {
		return batchesProcessed;
	}

	public abstract void batchProcessed(int rowsInThisBatch);

}
