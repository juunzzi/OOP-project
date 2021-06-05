package csv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ColumnImpl implements Column {
    List<String> columnData;
    String columnHeader;
    int columnIdx;
    boolean isFirstRowHeader;

    String columnDataType;
    long columnNumericCount;
    long columnNullCount;
    long columnCellCount;
    long columnNonNullCount;
    long columnMaxLength;

    double columnMean;
    double columnStd;
    double columnMin;
    double columnMax;
    double columnQ1;
    double columnMedian;
    double columnQ3;

    Object[] sortedArray;

    ColumnImpl() {

    }

    /**
     * @param header           혹은 double로 평가될 수 있는 cell의 개수
     * @param idx              혹은 double로 평가될 수 있는 cell의 개수
     * @param isFirstRowHeader 혹은 double로 평가될 수 있는 cell의 개수
     */
    ColumnImpl(String header, int idx, boolean isFirstRowHeader) {
        this.columnHeader = header;
        this.columnIdx = idx;
        this.columnData = new ArrayList<>();
        this.isFirstRowHeader = isFirstRowHeader;
        if (this.isFirstRowHeader) {
            this.columnData.add(header);
        }

    }

    ColumnImpl copy() {
        ColumnImpl ci = new ColumnImpl(this.columnHeader, this.columnIdx, this.isFirstRowHeader);
        for (String __ : this.columnData) {
            ci.columnData.add(__);
        }
        return ci;
    }

    void setColumnIndex(int newIndex) {
        this.columnIdx = newIndex;
    }

    void setColumnInfo() {
        this.columnDataType = this.getDataType();

        this.columnNumericCount = this.getNumericCount();

        this.columnNullCount = this.getNullCount();

        this.columnCellCount = this.count();

        this.columnNonNullCount = this.getNonNullCount();

        this.columnMaxLength = this.getColumnMaxLength();

        this.sortedArray = this.getSortedArray();

        this.columnMean = this.getMean();

        this.columnStd = this.getStd();

        this.columnMin = this.getNumericMin();

        this.columnMax = this.getNumericMax();

        this.columnQ1 = this.getQ1();

        this.columnMedian = this.getMedian();

        this.columnQ3 = this.getQ3();

        return;
    }

    Object[] getSortedArray() {
        ArrayList<Double> array = new ArrayList<>();
        int $_start = this.isFirstRowHeader ? 1 : 0;
        int $_last = this.columnData.size();
        for (int $ = $_start; $ < $_last; $++) {
            String __ = this.getValue($);
            if (!(__ == null)) {
                try {
                    double value = Double.parseDouble(__);
                    array.add(value);
                } catch (NumberFormatException e) {

                }

            }
        }
        Object[] arrayData = array.toArray();
        Arrays.sort(arrayData);
        return arrayData;
    }

    long getColumnMaxLength() {
        int max = this.columnHeader.length();
        for (String __ : this.columnData) {
            if (__ != null) {

                if (max < __.length()) {
                    max = __.length();
                }
            }
        }
        return max;
    }

    long getNonNullCount() {
        return this.columnCellCount - this.columnNullCount;
    }

    boolean isIntegerColumn() {
        int startCnt = this.isFirstRowHeader ? 1 : 0;
        int lastCnt = this.columnData.size();

        for (int $ = startCnt; $ < lastCnt; $++) {
            String __ = this.getValue($);
            try {
                if (!(__ == null)) {
                    Integer.parseInt(__);
                }
            } catch (NumberFormatException e) {
                // 하나라도 문자열이라면 => false;
                return false;
            }
        }
        return true;
    }

    String getDataType() {
        if (this.isNumericColumn()) {
            if (this.isIntegerColumn()) {
                return "int";
            } else {
                return "double";

            }

        } else {
            return "String";
        }

    }

    public String getHeader() {
        return this.columnHeader;
    };

    /* cell 값을 String으로 반환 */
    public String getValue(int index) {

        return this.columnData.get(index);
    };

    /**
     * @param index
     * @param t     가능한 값으로 Double.class, Integer.class
     * @return Double 혹은 Integer로 반환 불가능할 시, 예외 발생
     */
    public <T extends Number> T getValue(int index, Class<T> t) {
        return null;
    };

    public void setValue(int index, String value) {
        this.columnData.set(index, value);
        return;
    };

    /**
     * @param value double, int 리터럴을 index의 cell로 건네고 싶을 때 사용
     */
    public <T extends Number> void setValue(int index, T value) {
        this.columnData.set(index, "" + value);
        return;
    };

    /**
     * @return null 포함 모든 cell 개수 반환
     */
    public int count() {
        return this.columnData.size();
    };

    public void print() {
        return;
    }

    /**
     * @return (int or null)로 구성된 컬럼 or (double or null)로 구성된 컬럼이면 true 반환
     */
    public boolean isNumericColumn() {
        int $_start = this.isFirstRowHeader ? 1 : 0;
        int $_last = this.columnData.size();

        for (int $ = $_start; $ < $_last; $++) {
            String __ = this.getValue($);
            try {
                if (!(__ == null)) {
                    Double.parseDouble(__);
                }

            } catch (NumberFormatException e) {
                // 하나라도 문자열이라면 => false;
                return false;
            }
        }

        return true;
    };

    public long getNullCount() {
        long result = 0;
        for (String __ : this.columnData) {

            if (__ == null) {
                result++;
            }
        }
        return result;
    };

    /**
     * @return int 혹은 double로 평가될 수 있는 cell의 개수
     */
    public long getNumericCount() {
        int result = 0;
        for (String __ : this.columnData) {

            try {
                if (!(__ == null)) {
                    Double.parseDouble(__);
                    result++;
                }
            } catch (NumberFormatException e) {

            }
        }
        return result;
    }

    // 아래 7개 메소드는 String 타입 컬럼에 대해서 수행 시, 예외 발생 시켜라.
    public double getNumericMin() {
        if (this.columnNumericCount > 0) {

            return (double) this.sortedArray[0];
        } else {
            return 0.0 / 0.0;
        }
    };

    public double getNumericMax() {
        if (this.columnNumericCount > 0) {
            return (double) this.sortedArray[this.sortedArray.length - 1];

        } else {
            return 0.0 / 0.0;
        }
    }

    public double getMean() {
        if (this.columnNumericCount > 0) {

            double result = 0;
            int $_start = this.isFirstRowHeader ? 1 : 0;
            int $_last = this.columnData.size();
            for (int $ = $_start; $ < $_last; $++) {
                String __ = this.getValue($);
                if (!(__ == null)) {
                    try {

                        result += Double.parseDouble(__);
                    } catch (NumberFormatException e) {

                    }

                }
            }
            return result / this.columnNumericCount;
        } else {
            return 0.0 / 0.0;
        }

    }

    public double getStd() {
        if (this.columnNumericCount > 0) {
            double result = 0;
            double mean = this.columnMean;

            int $_start = this.isFirstRowHeader ? 1 : 0;
            int $_last = this.columnData.size();
            int count = 0;
            for (int $ = $_start; $ < $_last; $++) {
                String __ = this.getValue($);

                if (!(__ == null)) {
                    try {
                        double value = Double.parseDouble(__) - mean;
                        result += Math.pow(value, 2);
                        count++;
                    } catch (NumberFormatException e) {

                    }

                }
            }
            double v = result / (this.columnNumericCount - 1);

            return Math.sqrt(v);
        } else {
            return 0.0 / 0.0;

        }

    }

    public double getQ1() {

        if (this.columnNumericCount > 0) {
            int allLength = this.sortedArray.length;
            Object[] lowHalf = Arrays.copyOfRange(this.sortedArray, 0, allLength / 2);
            int median = lowHalf.length / 2;
            if (lowHalf.length % 2 == 0) {
                return ((double) lowHalf[median - 1] + (double) lowHalf[median]) / 2;
            } else {
                return (double) lowHalf[median];
            }
            // return ((double) this.sortedArray[median - 1] + (double)
            // this.sortedArray[median]) / 2;

        } else {

            return 0.0 / 0.0;
        }

    }

    public double getMedian() {
        if (this.columnNumericCount > 0) {
            int sortedArrayLength = this.sortedArray.length;
            if (sortedArrayLength > 1) {

                if (sortedArrayLength % 2 == 0) {
                    int medianIdx = (int) (sortedArrayLength / 2);
                    this.columnMedian = (((double) sortedArray[medianIdx - 1] + (double) sortedArray[medianIdx]) / 2);

                } else {
                    int medianIdx = (int) (sortedArray.length / 2);
                    this.columnMedian = (double) sortedArray[medianIdx];

                }
            } else {
                if (sortedArrayLength == 1) {
                    this.columnMedian = (double) sortedArray[0];
                }
            }
            return this.columnMedian;
        } else {
            return 0.0 / 0.0;
        }

    }

    public double getQ3() {
        if (this.columnNumericCount > 0) {

            int median = this.sortedArray.length / 2;
            if (this.sortedArray.length % 2 == 0) {

                Object[] upperArray = Arrays.copyOfRange(this.sortedArray, median, this.sortedArray.length);
                median = upperArray.length / 2;
                if (upperArray.length % 2 == 0) {
                    return ((double) upperArray[median - 1] + (double) upperArray[median]) / 2;
                } else {
                    return (double) upperArray[median];

                }

            } else {

                Object[] upperArray = Arrays.copyOfRange(this.sortedArray, median + 1, this.sortedArray.length);
                median = upperArray.length / 2;
                if (upperArray.length % 2 == 0) {
                    return ((double) upperArray[median - 1] + (double) upperArray[median]) / 2;
                } else {
                    return (double) upperArray[median];

                }
            }
        } else {

            return 0.0 / 0.0;
        }

    }

    // 아래 2개 메소드는 1개 cell이라도 치환했으면, true 반환.
    public boolean fillNullWithMean() {
        boolean result = false;
        if (this.columnDataType.equals("String")) {
            // 아무것도 안함.
        } else {
            result = true;
            int $_last = this.columnData.size();
            for (int $ = 0; $ < $_last; $++) {
                String __ = this.getValue($);
                if (__ == null) {

                    String mean = Double.toString(this.columnMean);
                    this.setValue($, mean);

                }
            }
        }
        return result;

    }

    public boolean fillNullWithZero() {
        boolean result = false;
        if (this.columnDataType.equals("String")) {
            // 아무것도 안함.
        } else {
            result = true;
            int $_last = this.columnData.size();
            for (int $ = 0; $ < $_last; $++) {
                String __ = this.getValue($);
                if (__ == null) {
                    if (this.columnDataType.equals("int")) {
                        this.setValue($, "0");
                    }
                    if (this.columnDataType.equals("double")) {
                        this.setValue($, "0.0");
                    }
                }
            }
        }
        return result;

    }

    // 아래 3개 메소드는 null 값은 메소드 호출 후에도 여전히 null.
    // standardize()와 normalize()는 String 타입 컬럼에 대해서는 false 반환
    // factorize()는 컬럼 타입과 무관하게 null 제외하고 2가지 값만으로 구성되었다면 수행된다. 조건에 부합하여 수행되었다면 true
    // 반환
    public boolean standardize() {
        boolean result = false;
        if (this.columnDataType.equals("String")) {

        } else {
            // 문자열이 아닌것만 수행한다.
            result = true;
            double mean = this.columnMean;
            double std = this.columnStd;
            int $_last = this.columnData.size();
            for (int $ = 0; $ < $_last; $++) {
                String __ = this.getValue($);
                if (!(__ == null)) {

                    try {

                        double target = Double.parseDouble(this.getValue($));
                        target = (target - mean) / (std);

                        this.setValue($, target);
                    } catch (NumberFormatException e) {

                    }
                }

            }

        }
        return result;

    }

    public boolean normalize() {
        boolean result = false;
        if (this.columnDataType.equals("String")) {
            return false;
        } else {
            // 문자열이 아닌것만 수행한다.
            result = true;
            double max = this.columnMax;
            double min = this.columnMin;
            int $_last = this.columnData.size();
            for (int $ = 0; $ < $_last; $++) {
                String __ = this.getValue($);
                if (!(__ == null)) {
                    try {

                        double target = Double.parseDouble(__);
                        target = (target - min) / (max - min);

                        this.setValue($, target);
                    } catch (NumberFormatException e) {

                    }
                }

            }

        }
        return result;

    }

    public boolean factorize() {
        boolean result = false;

        List<String> li = new ArrayList<>();

        for (String __ : this.columnData) {
            if (li.contains(__)) {

            } else {
                li.add(__);
            }
        }

        if (li.size() == 2) {
            if (li.contains("0") && li.contains("1")) {
                return false;
            } else {

                result = true;
                int $_last = this.columnData.size();
                for (int $ = 0; $ < $_last; $++) {
                    String __ = this.getValue($);
                    if (__.equals(li.get(0))) {
                        this.setValue($, "0");

                    } else {
                        this.setValue($, "1");
                    }
                }
            }
        }
        return result;

    }
}

/*
 * 
 * private double getQuartile(int q) { // make double array List<Double> doubles
 * = new ArrayList<>(); for (int i = 0; i < count(); ++i) { try {
 * doubles.add(getValue(i, Double.class)); } catch (NumberFormatException e) {
 * // Not a Number } } Collections.sort(doubles); // sort
 * 
 * // get quartile double index = q * (doubles.size() - 1) / 4.0; int lower =
 * (int) Math.floor(index);
 * 
 * if (lower < 0) return doubles.get(0); // defensive code
 * 
 * if (lower >= doubles.size() - 1) return doubles.get(doubles.size() - 1);
 * 
 * double fraction = index - lower; return doubles.get(lower) + fraction *
 * (doubles.get(lower + 1) - doubles.get(lower)); }
 */