import java.util.List;


public class Matrix {

    private FiniteField_F_2_n finiteField = null;

    private List<Packet> rows;

    public Matrix(List<Packet> rows, FiniteField_F_2_n finiteField) {
        this.rows = rows;
        this.finiteField = finiteField;
    }

    public void performGaussianElimination() {

        for (int i = 0; i < rows.size(); i++) {

            // convert to leading non-zero
            convertToLeadingNonZero(i);

            // convert to leading one
            finiteField.multiplyPacketBy(rows.get(i), finiteField.complementByOne(rows.get(i).header[i]));

            // and other zeros
            for (int j = i+1; j < rows.size(); j++) {
                finiteField.addPackets(rows.get(j), finiteField.multiplyPacketBy(rows.get(i).clone(), rows.get(j).header[i]));
            }
        }

        /// Matrix is now upper triangular.

        for (int i = rows.size() - 1; i >= 0; i--) {
            /// Starting with the last row in the matrix. Each row stored as a packet object.
            Packet packetToAdd = rows.get(i);
            /// For each of the preceding rows (w.r.t the current row) multiply each preceding row by its ith header
            //  element. Add the result to the preceding row.
            for (int j = i - 1; j >= 0; j--) {
                finiteField.addPackets(rows.get(j), finiteField.multiplyPacketBy(packetToAdd.clone(), rows.get(j).header[i]));
            }
        }

        //System.out.println("RESULT: " + rows);
    }

    public List<Packet> getOriginalPackets() {
        return rows;
    }

    private void convertToLeadingNonZero() {
        convertToLeadingNonZero(0);
    }

    /// convertToLeadingNonZero Rearranges the matrix so that the first row
    //  in the matrix has a non-zero value at row 1 col 1
    //  (numbers above refer to positions in the matrix as opposed to their corresponding array indices)
    private void convertToLeadingNonZero(int position) {
        if (position >= rows.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (rows.get(position).header[position] == 0) {
            for (int i = position; i < rows.size(); i++) {
                if (rows.get(i).header[i] != 0) {
                    Packet temp = rows.remove(i);
                    rows.add(position, temp);
                }
            }
        }
    }
}
