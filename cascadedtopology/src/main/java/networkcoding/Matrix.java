package networkcoding;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


class Matrix {

    private FiniteField_F_2_n finiteField = null;

    private List<Packet> rows;

    Matrix(List<Packet> rows, FiniteField_F_2_n finiteField) {
        this.rows = rows;
        this.finiteField = finiteField;
    }

    void performGaussianElimination() {

        Logger logger = Logger.getLogger("log");

        for (int i = 0; i < rows.size(); i++) {
            int leading_one ;

            // convert to leading non-zero
            convertToLeadingNonZero(i);

            // check: is the first non-zero entry in the encoding row one?
            if( (leading_one = check_for_leading_one( rows.get(i))) < 0){
                // a leading one was not found
                // transform the encoding row so that it has a leading one
                // multiply the entry on the main diagonal by its inverse
                finiteField.multiplyPacketBy(rows.get(i), finiteField.complementByOne(rows.get(i).header[i]));
                leading_one = check_for_leading_one(rows.get(i));
            }
            // check: is the rest of the encoding row 0's?
            if( check_for_zeros( rows.get(i), leading_one) == 0){
                // no, some of the encoding row is non-zero
                // loop through the rest of the encoding row and eliminate non-zero's
                for (int j = i+1; j < rows.size(); j++) {
                    // add to each jth row the ith row multiplied by the header
                    finiteField.addPackets(
                            rows.get(j),
                            finiteField.multiplyPacketBy(rows.get(i).clone(), rows.get(j).header[i]));
                }
            }
        }

        /// Matrix is now upper triangular.
        // logger.log(Level.INFO, String.format("Upper Triangular: %s", rows) );

        for (int i = rows.size() - 1; i >= 0; i--) {
            /// Starting with the last row in the matrix. Each row stored as a packet object.
            // check: is the encoding vector equal to a row in the identity matrix?
            if( check_for_identity_equivalence( rows.get(i)) == 0){
                Packet packetToAdd = rows.get(i);
                /// For each of the preceding rows (w.r.t the current row) multiply each preceding row by its ith header
                //  element. Add the result to the preceding row.
                for (int j = i - 1; j >= 0; j--) {
                    finiteField.addPackets(
                            rows.get(j),
                            finiteField.multiplyPacketBy(packetToAdd.clone(), rows.get(j).header[i]));
                }
            }
        }
        // logger.log(Level.INFO, String.format("Decoded: %s", rows));
    }

    List<Packet> getOriginalPackets() {
        return rows;
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
                    Collections.swap(rows, position, i);
                    break;
                }
            }
        }
    }

    private int check_for_leading_one( Packet row){
        // loop through each component of the header row.
        // if first non-zero entry in header row is the number one return its index.
        // otherwise return -1.
        for( int index = 0; index < row.header.length ; index++ ){
            // check whether the component is a one
            if( row.header[index] == 1 ){
                return index;
            }
            // continue checking for a leading one only if current element is zero
            else if( row.header[index] != 0 ){
                // the first non-zero element is not one, return -1
                break;
            }
            else if( ( index == row.header.length - 1 ) ){ // row is all zero's
                return -2;
            }
        }
        return -1;
    }

    private int check_for_zeros( Packet row, int one){
        // starting at the index of the leading one, check the remaining entries in the header row.
        // if all remaining entries are zero, return 1.
        // if a non-zero entry exists return 0.
        if( one == -2) { //
            return 1;
        }
        if( one == -1){
            return 1;
        }
        int element = row.header[one];
        for( ; one < row.header.length ; element = row.header[one++]){
            if( element != 0){
                return 0;
            }
        }
        return 1;
    }

    private int check_for_identity_equivalence( Packet row){
        int index;
        if( (index = check_for_leading_one(row)) > 0){
            if( check_for_zeros(row, index) == 1 ){
                return 1;
            }
        }
        return 0;
    }
}
