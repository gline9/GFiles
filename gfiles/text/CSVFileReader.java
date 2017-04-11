package gfiles.text;

/**
 * helper class for reading values from a normally formatted csv file.
 * 
 * @author Gavin
 *
 */
public class CSVFileReader {

	// file to read from
	private final CSVFile file;

	// stores how many lines the csv file has. -1 is default so the program
	// knows to compute if it hasn't already.
	private int lines = -1;

	// string array for holding each of the values of the file.
	private String[][] data;

	// boolean for whether the file has titles on the top row or not.
	private boolean hasTitles = true;

	/**
	 * basic constructor that takes in the file to read from
	 * 
	 * @param file
	 *            file to read from
	 */
	public CSVFileReader(CSVFile file) {
		this.file = file;

		// initialize the data string array.
		init();
	}

	/**
	 * initializes the csv file reader to have an array of the values for faster
	 * look up than using the file.
	 */
	private void init() {
		// initialize the data to an array of length the number of lines in the
		// csv file
		data = new String[lines()][];

		// loop through and set all of the data points in the array.
		for (int i = 0; i < lines(); i++) {
			data[i] = file.nextLine();
		}
	}

	/**
	 * refreshes the internal data on the csv file
	 */
	public void refreshData() {
		// reset the number of lines so it can be recalculated.
		lines = -1;

		// re-initialize the data array
		init();
	}

	/**
	 * gets the number of lines that are in the csv file.
	 * 
	 * @return number of lines in the csv file.
	 */
	public int lines() {
		if (lines == -1) {

			// reset the lines counter to 0
			lines = 0;

			// loop through and increment for each line.
			String[] line = file.nextLine();

			// check to make sure the line isn't the end of file
			while (line != null) {

				// increment the line counter
				lines++;

				// get the next line
				line = file.nextLine();
			}

			// reset the file back to the beginning.
			file.resetLine();
		}

		// return the number of lines in the file
		return lines;
	}

	/**
	 * set whether the csv file being read from has a first line that indicates
	 * what the entries below are.
	 * 
	 * @param titles
	 *            if there are titles or not.
	 */
	public void setTitles(boolean titles) {
		hasTitles = titles;
	}

	/**
	 * returns the titles at the top of the csv file. If specified as no titles
	 * will return null.
	 * 
	 * @return first line of the csv file.
	 */
	public String[] getTitles() {
		// if no titles return null
		if (!hasTitles)
			return null;

		// otherwise return the first row.
		return data[0];
	}

	/**
	 * returns the entry located at the xth column and the yth row of the csv.
	 * If there are titles in the first row it will ignore it so x = 0, y = 0
	 * will give the first actuall data point not a title.
	 * 
	 * @param x
	 *            x location to look
	 * @param y
	 *            y location to look
	 * @return the entry at that position.
	 */
	public String getEntry(int x, int y) {
		// if there are titles increment y by 1
		if (hasTitles) {
			// make sure the element is in bounds
			if (y + 1 >= data.length || x >= data[y + 1].length) {
				return null;
			}
			return data[y + 1][x];
		}

		// otherwise just return the position

		// make sure the element is in bounds
		if (y >= data.length || x >= data[y].length)
			return null;
		return data[y][x];
	}

	/**
	 * returns the entry located at the row specified with the title that you
	 * give. If there aren't titles in the current csv it will return null. If
	 * the title doesn't exist in the csv it will also return null.
	 * 
	 * @param title
	 *            title of the column
	 * @param row
	 *            row to look at starting at 0
	 * @return the entry at that position.
	 */
	public String getEntry(String title, int row) {
		// if there aren't titles return null.
		if (!hasTitles)
			return null;

		// look through the titles and find the column that the title is located
		// at, if not found return null.
		int position = 0;
		String[] titles = getTitles();
		for (String currentTitle : titles) {
			// if the index we are at is the correct one break out of loop.
			if (currentTitle.equals(title)) {
				break;
			}

			// if the index we are at isn't go to the next.
			position++;
		}

		// if we looped through and didn't find it return null.
		if (position == titles.length)
			return null;

		// otherwise return the element at position, row
		return getEntry(position, row);

	}

	public String getEntry(String title, String indexHeader, String index) {
		// if there aren't titles return null.
		if (!hasTitles)
			return null;

		// search through titles and find the column that the title is located
		// at.
		int titlePosition = 0;
		String[] titles = getTitles();
		for (String currentTitle : titles) {
			// if the index we are at is the correct one break out of loop.
			if (currentTitle.equals(title)) {
				break;
			}

			// if the index we are at isn't go to the next.
			titlePosition++;
		}
		if (titlePosition == titles.length)
			return null;

		// search through titles and find the column that the index is located
		// at.
		int indexPosition = 0;
		for (String currentTitle : titles) {
			// if the index we are at is the correct one break out of loop.
			if (currentTitle.equals(indexHeader)) {
				break;
			}

			// if the index we are at isn't go to the next.
			indexPosition++;
		}
		if (indexPosition == titles.length)
			return null;

		// search through the column marked by the index in search for the index
		int y = 0;
		for (; y < data[0].length; y++) {
			String test = getEntry(indexPosition, y);
			if (test == null)
				return null;
			if (test.equals(index))
				break;
		}
		if (y == data[0].length)
			return null;

		// return the found element
		return getEntry(titlePosition, y);
	}

}
