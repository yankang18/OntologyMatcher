package umbc.csee.ebiquity.ontologymatcher.algorithm.component;


public interface ISimilarityMatrix {

	void init(int row, int col);
	
	SimilarityMatrixLabel getRow(int i);

	SimilarityMatrixLabel getCol(int i);

	SimilarityMatrixCell getCellAt(int i, int j);

	double getSimilarityAt(int i, int j);

	String getRowName(int i);

	String getColName(int i);

	int getRowCount();

	int getColCount();

	Object getRowObject(int i);

	Object getColObject(int i);
	
	/**
	 * given data      : a-b-c-d-e-g <-> A-D-F-G
	 * matching boolean: 1-0-0-1-0-1 <-> 1-1-0-1
	 * @return
	 */
	Boolean[] getRowMatching();
	
	Boolean[] getColMatching();
	
}