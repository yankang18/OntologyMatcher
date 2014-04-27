package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.util.HashMap;

public class SimilarityMatrix implements ISimilarityMatrix {

	private HashMap<Integer, SimilarityMatrixLabel> rowList = new HashMap<Integer, SimilarityMatrixLabel>();
	private HashMap<Integer, SimilarityMatrixLabel> colList = new HashMap<Integer, SimilarityMatrixLabel>();
//	private HashMap<Integer, HashMap<Integer, SimilarityMatrixCell>> cellMap = new HashMap<Integer, HashMap<Integer, SimilarityMatrixCell>>();
	private HashMap<Long, SimilarityMatrixCell> cellMap = new HashMap<Long, SimilarityMatrixCell>();
	private Boolean[] rowMatchArray;
	private Boolean[] colMatchArray;

	public SimilarityMatrix(int rSize, int cSize) {
		init(rSize, cSize);
	}
	
	private long getMapID(int i, int j) {
		return i*colList.size()+j;
	}
	
	public void setCellAt(int i, int j, SimilarityMatrixCell cell) {
		rowMatchArray[i] = true;
		colMatchArray[j] = true;
		cellMap.put(getMapID(i,j), cell);
	}

	@Override
	public SimilarityMatrixCell getCellAt(int i, int j) {
		return cellMap.get(getMapID(i,j));
	}

	public void setCol(int i, SimilarityMatrixLabel smLabel) {
		colList.put(i, smLabel);
	}
		
	@Override
	public SimilarityMatrixLabel getCol(int i) {
		return colList.get(i);
	}

	@Override
	public int getColCount() {
		return colList.size();
	}

	@Override
	public String getColName(int i) {
		return colList.get(i).getLocalName();
	}

	@Override
	public Object getColObject(int i) {
		return colList.get(i).getObject();
	}

	public void setRow(int i, SimilarityMatrixLabel smLabel) {
		rowList.put(i, smLabel);
	}
		
	@Override
	public SimilarityMatrixLabel getRow(int i) {
		return rowList.get(i);
	}

	@Override
	public int getRowCount() {
		return rowList.size();
	}

	@Override
	public String getRowName(int i) {
		return rowList.get(i).getLocalName();
	}

	@Override
	public Object getRowObject(int i) {
		return rowList.get(i).getObject();
	}

	@Override
	public double getSimilarityAt(int i, int j) {
		SimilarityMatrixCell cell = cellMap.get(getMapID(i,j));

		return cell.getSimilarity();
	}

	@Override
	public Boolean[] getRowMatching() {
		return rowMatchArray;
	}

	@Override
	public Boolean[] getColMatching() {
		return colMatchArray;
	}

	int rSize = 0;
	int cSize = 0;
	
	@Override
	public void init(int rSize, int cSize) {
		this.rSize = rSize;
		this.cSize = cSize;
		rowMatchArray = new Boolean[rSize];
		for (int i = 0; i < rSize; i++) {
			rowMatchArray[i] = false;
		}
		colMatchArray = new Boolean[cSize];
		for (int j = 0; j < cSize; j++) {
			colMatchArray[j] = false;
		}
	}
}