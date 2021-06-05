package csv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Predicate;

class TableImpl implements Table {
    List<ColumnImpl> tableData;
    int tableRowCount;
    int tableColumnCount;
    int tableBeginIdx;
    int tableLastIdx;
    int tableHeaderMaxLength;
    int doubleColumnCount;
    int integerColumnCount;
    int stringColumnCount;
    boolean isFirstRowHeader;

    TableImpl() {

    }

    TableImpl(List<ColumnImpl> li) {
        this.tableData = li;
    }

    public int getRowCount() {
        return this.tableData.get(0).columnData.size();
    }

    public int getColumnCount() {
        return this.tableData.size();
    }

    public Column getColumn(int index) {
        return this.tableData.get(index);
    }

    public Column getColumn(String name) {
        for (ColumnImpl __ : this.tableData) {
            if (__.columnHeader.equals(name)) {
                return __;
            }
        }
        return null;
    }

    Table copy() {
        List<ColumnImpl> cl = new ArrayList<>();
        for (ColumnImpl oldColumn : this.tableData) {
            cl.add(oldColumn.copy());
        }
        TableImpl ti = new TableImpl(cl);
        ti.setTableInfo(this.isFirstRowHeader);
        return ti;
    }

    void setTableInfo(boolean isFirstRowHeader) {
        this.isFirstRowHeader = isFirstRowHeader;
        this.tableColumnCount = this.getColumnCount();
        this.tableRowCount = this.getRowCount();
        this.tableBeginIdx = 0;
        this.tableLastIdx = this.tableRowCount - 1;
        for (ColumnImpl __ : tableData) {
            __.setColumnInfo();
        }
        for (ColumnImpl __ : this.tableData) {
            if (__.columnDataType.equals("double")) {
                this.doubleColumnCount++;
            }
            if (__.columnDataType.equals("int")) {
                this.integerColumnCount++;

            }
            if (__.columnDataType.equals("String")) {
                this.stringColumnCount++;
            }
        }
        return;
    }

    @Override
    public String toString() {
        String result = "";
        int tableHeaderMaxLength = this.getTableHeaderMaxLength();
        result += "<csv.Table@" + Integer.toHexString(this.tableData.hashCode()) + ">\n";
        result += "RangeIndex: " + this.tableRowCount + " entries, " + this.tableBeginIdx + " to " + this.tableLastIdx
                + "\n";
        result += "Data columns " + "(total " + this.tableColumnCount + " columns) :" + "\n";
        result += String.format(" # |%" + tableHeaderMaxLength + "s|Non-Null Count |Dtype\n", "Columns");
        for (ColumnImpl __ : this.tableData) {
            result += String.format("%2d |%" + tableHeaderMaxLength + "s| %4d non-null |%s\n", __.columnIdx,
                    __.columnHeader, __.columnNonNullCount, __.columnDataType);
        }

        result += "dtypes : double(" + this.doubleColumnCount + ")" + ", int(" + this.integerColumnCount + "), String("
                + this.stringColumnCount + ")\n";
        return result;
    }

    int getTableHeaderMaxLength() {
        int max = 0;
        for (ColumnImpl __ : this.tableData) {
            int chLength = __.columnHeader.length();
            if (max < chLength) {
                max = chLength;
            }
        }
        return max;
    }

    public void print() {

        String result = "";

        for (ColumnImpl __ : this.tableData) {
            result += String.format("%" + __.columnMaxLength + "s|", __.columnHeader);

        }
        result += "\n";
        int $_last = this.tableRowCount;
        for (int $ = 0; $ < $_last; $++) {
            for (ColumnImpl __ : this.tableData) {
                result += String.format("%" + ((__.columnMaxLength > 4) ? __.columnMaxLength : 4) + "s|",
                        __.getValue($));

            }
            result += "\n";
        }
        System.out.println(result);
        return;

    }

    @Override
    public Table getStats() {
        int $ = 0;
        List<ColumnImpl> cl = new ArrayList<>();
        cl.add(new ColumnImpl("", $++, false));
        cl.get($ - 1).columnData.add("count");
        cl.get($ - 1).columnData.add("mean");
        cl.get($ - 1).columnData.add("std");
        cl.get($ - 1).columnData.add("min");
        cl.get($ - 1).columnData.add("25%");
        cl.get($ - 1).columnData.add("50%");
        cl.get($ - 1).columnData.add("75%");
        cl.get($ - 1).columnData.add("max");

        for (ColumnImpl __ : this.tableData) {
            if (__.columnNumericCount != 0) {
                // 값들이 존재하므로ß
                cl.add(new ColumnImpl(__.columnHeader, $++, false));
                cl.get($ - 1).columnData.add("" + __.columnNumericCount);
                cl.get($ - 1).columnData.add("" + __.columnMean);
                cl.get($ - 1).columnData.add("" + __.columnStd);
                cl.get($ - 1).columnData.add("" + __.columnMin);
                cl.get($ - 1).columnData.add("" + __.columnQ1);
                cl.get($ - 1).columnData.add("" + __.columnMedian);
                cl.get($ - 1).columnData.add("" + __.columnQ3);
                cl.get($ - 1).columnData.add("" + __.columnMax);

            }
        }
        TableImpl table = new TableImpl(cl);
        table.setTableInfo(false);

        return table;
    }

    @Override
    public Table head() {
        List<ColumnImpl> cl = new ArrayList<>();

        int $_lastColumnCount = this.tableColumnCount;
        int $_lastRowCount = (this.tableRowCount >= 5) ? 5 : this.tableRowCount;
        // 테이블에 넣을 모든 컬럼 생성완료
        for (int $_c = 0; $_c < $_lastColumnCount; $_c++) {
            ColumnImpl temp = this.tableData.get($_c);
            cl.add(new ColumnImpl(temp.columnHeader, $_c, temp.isFirstRowHeader));
        }
        $_lastColumnCount = cl.size();
        // 컬럼에 데이터 삽입.
        for (int $_c = 0; $_c < $_lastColumnCount; $_c++) {
            ColumnImpl oldTableColumn = this.tableData.get($_c);
            ColumnImpl newTableColumn = cl.get($_c);
            for (int $_r = 0; $_r < $_lastRowCount; $_r++) {
                String target = oldTableColumn.getValue($_r);
                newTableColumn.columnData.add(target);

            }
        }
        TableImpl table = new TableImpl(cl);
        table.setTableInfo(this.isFirstRowHeader);
        return table;
    }

    @Override
    public Table head(int lineCount) {

        int $_lastColumnCount = this.tableColumnCount;
        int $_lastRowCount = this.tableRowCount;
        if ($_lastRowCount < lineCount) {
            System.out.println("해당 테이블은 line Count보다 행의 갯수가 적습니다");
            return null;
        } else {
            $_lastRowCount = lineCount;
            List<ColumnImpl> cl = new ArrayList<>();
            // 테이블에 넣을 모든 컬럼 생성완료
            for (int $_c = 0; $_c < $_lastColumnCount; $_c++) {
                ColumnImpl temp = this.tableData.get($_c);
                cl.add(new ColumnImpl(temp.columnHeader, $_c, temp.isFirstRowHeader));
            }
            $_lastColumnCount = cl.size();
            // 컬럼에 데이터 삽입.
            for (int $_c = 0; $_c < $_lastColumnCount; $_c++) {
                ColumnImpl oldTableColumn = this.tableData.get($_c);
                ColumnImpl newTableColumn = cl.get($_c);
                for (int $_r = 0; $_r < $_lastRowCount; $_r++) {
                    String target = oldTableColumn.getValue($_r);
                    newTableColumn.columnData.add(target);

                }
            }
            TableImpl table = new TableImpl(cl);
            table.setTableInfo(this.isFirstRowHeader);
            return table;
        }
    }

    @Override
    public Table tail() {
        int $_lastColumnCount = this.tableColumnCount;
        int $_lastRowCount = this.tableRowCount;
        int $_startRowCount = (($_lastRowCount >= 5) ? $_lastRowCount - 5 : 0);
        List<ColumnImpl> cl = new ArrayList<>();
        // 테이블에 넣을 모든 컬럼 생성완료
        for (int $_c = 0; $_c < $_lastColumnCount; $_c++) {
            ColumnImpl temp = this.tableData.get($_c);
            cl.add(new ColumnImpl(temp.columnHeader, $_c, temp.isFirstRowHeader));
        }
        $_lastColumnCount = cl.size();
        // 컬럼에 데이터 삽입.
        for (int $_c = 0; $_c < $_lastColumnCount; $_c++) {
            ColumnImpl oldTableColumn = this.tableData.get($_c);
            ColumnImpl newTableColumn = cl.get($_c);
            for (int $_r = $_startRowCount; $_r < $_lastRowCount; $_r++) {
                String target = oldTableColumn.getValue($_r);
                newTableColumn.columnData.add(target);

            }
        }
        TableImpl table = new TableImpl(cl);
        table.setTableInfo(this.isFirstRowHeader);
        return table;
    }

    @Override
    public Table tail(int lineCount) {
        int $_lastColumnCount = this.tableColumnCount;
        int $_lastRowCount = this.tableRowCount;
        int $_startRowCount = (($_lastRowCount >= lineCount) ? $_lastRowCount - lineCount : 0);

        List<ColumnImpl> cl = new ArrayList<>();
        // 테이블에 넣을 모든 컬럼 생성완료
        for (int $_c = 0; $_c < $_lastColumnCount; $_c++) {
            ColumnImpl temp = this.tableData.get($_c);
            cl.add(new ColumnImpl(temp.columnHeader, $_c, temp.isFirstRowHeader));
        }
        $_lastColumnCount = cl.size();
        // 컬럼에 데이터 삽입.
        for (int $_c = 0; $_c < $_lastColumnCount; $_c++) {
            ColumnImpl oldTableColumn = this.tableData.get($_c);
            ColumnImpl newTableColumn = cl.get($_c);
            for (int $_r = $_startRowCount; $_r < $_lastRowCount; $_r++) {
                String target = oldTableColumn.getValue($_r);
                newTableColumn.columnData.add(target);

            }
        }
        TableImpl table = new TableImpl(cl);
        table.setTableInfo(this.isFirstRowHeader);
        return table;
    }

    @Override
    public Table selectRows(int beginIndex, int endIndex) {
        int $_lastColumnCount = this.tableColumnCount;

        List<ColumnImpl> cl = new ArrayList<>();
        // 테이블에 넣을 모든 컬럼 생성완료
        for (int $_c = 0; $_c < $_lastColumnCount; $_c++) {
            ColumnImpl temp = this.tableData.get($_c);
            cl.add(new ColumnImpl(temp.columnHeader, $_c, temp.isFirstRowHeader));
        }
        $_lastColumnCount = cl.size();
        // 컬럼에 데이터 삽입.
        for (int $_c = 0; $_c < $_lastColumnCount; $_c++) {
            ColumnImpl oldTableColumn = this.tableData.get($_c);
            ColumnImpl newTableColumn = cl.get($_c);
            for (int $_r = beginIndex; $_r < endIndex; $_r++) {
                String target = oldTableColumn.getValue($_r);
                newTableColumn.columnData.add(target);

            }
        }
        TableImpl table = new TableImpl(cl);
        table.setTableInfo(this.isFirstRowHeader);
        return table;
    }

    @Override
    public Table selectRowsAt(int... indices) {
        int $_lastColumnCount = this.tableColumnCount;

        List<ColumnImpl> cl = new ArrayList<>();
        // 테이블에 넣을 모든 컬럼 생성완료
        for (int $_c = 0; $_c < $_lastColumnCount; $_c++) {

            ColumnImpl temp = this.tableData.get($_c);
            cl.add(new ColumnImpl(temp.columnHeader, $_c, temp.isFirstRowHeader));

        }

        $_lastColumnCount = cl.size();
        // 컬럼에 데이터 삽입.
        for (int $_c = 0; $_c < $_lastColumnCount; $_c++) {

            ColumnImpl oldTableColumn = this.tableData.get($_c);
            ColumnImpl newTableColumn = cl.get($_c);

            for (int __ : indices) {
                String target = oldTableColumn.getValue(__);
                newTableColumn.columnData.add(target);
            }

        }

        TableImpl table = new TableImpl(cl);
        table.setTableInfo(this.isFirstRowHeader);
        return table;
    }

    @Override

    public <T> Table selectRowsBy(String columnName, Predicate<T> predicate) {
        // string double integer object
        ColumnImpl ci = (ColumnImpl) this.getColumn(columnName);
        List<Integer> checkedArray = new ArrayList<>();
        int $_lastRowCount = this.tableRowCount;
        for (int $_r = 0; $_r < $_lastRowCount; $_r++) {
            String __string = ci.getValue($_r);
            try {
                if (predicate.test((T) __string)) {
                    checkedArray.add($_r);
                }
            } catch (Exception e) {
                // 숫자인것이므로 프레디케이트가 더블이나 인티거

                if (!(__string == null)) {

                    Double __double = Double.parseDouble(__string);
                    try {
                        if (predicate.test((T) __double)) {
                            checkedArray.add($_r);

                        }
                    } catch (Exception ef) {
                        try {
                            Integer __integer = Integer.parseInt(__string);
                            // 인티거로 비교하는데 더블값오면 예외처리해주면되나 아니면 어떻게든 계산해주어야하나 ?
                            if (predicate.test((T) __integer)) {
                                checkedArray.add($_r);

                            }
                        } catch (NumberFormatException ex) {

                        }
                    }
                }

                // System.out.println(predicate.getClass().getGenericInterfaces());
            }
        }
        int[] array = checkedArray.stream().mapToInt(i -> i).toArray();
        return this.selectRowsAt(array);

    }

    @Override
    public Table selectColumns(int beginIndex, int endIndex) {
        List<ColumnImpl> cl = new ArrayList<>();
        for (int $ = beginIndex, columnIndex = 0; $ < endIndex; columnIndex++, $++) {
            ColumnImpl oldColumn = this.tableData.get($);
            oldColumn.setColumnIndex(columnIndex);
            cl.add(oldColumn);
        }
        TableImpl table = new TableImpl(cl);
        table.setTableInfo(this.isFirstRowHeader);

        return table;
    }

    @Override
    public Table selectColumnsAt(int... indices) {

        List<ColumnImpl> cl = new ArrayList<>();
        int columnIndex = 0;
        for (int $ : indices) {
            ColumnImpl oldColumn = this.tableData.get($);
            oldColumn.setColumnIndex(columnIndex++);
            cl.add(oldColumn);
        }
        TableImpl table = new TableImpl(cl);
        table.setTableInfo(this.isFirstRowHeader);

        return table;
    }

    // 테이블을 기준 열 인덱스로 정렬한다. true/ null값은 나중에 false
    public Table sort(int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {
        /* 값을 참조할 카피테이블 */
        TableImpl ti = (TableImpl) this.copy();

        /* 열 인덱스 정렬 */
        ColumnImpl ci = this.tableData.get(byIndexOfColumn);
        HashMap<Integer, String> map = new HashMap<>();
        int $_last = ci.columnData.size();

        for (int $ = 0; $ < $_last; $++) {
            String __ = ci.getValue($);
            if (__ == null) {

                if (isNullFirst) {
                    // 널이 앞으로
                    if (ci.columnDataType.equals("String")) {
                        __ = "\0";
                        map.put($, __);

                    } else {
                        __ = "0";
                        map.put($, __);

                    }
                } else {
                    // 널이 뒤로
                    if (ci.columnDataType.equals("String")) {
                        __ = "࿚";
                        map.put($, __);

                    } else {

                        __ = Double.toString(ci.columnMax + 1);
                        map.put($, __);

                    }
                }
            } else {
                map.put($, __);

            }

        }
        List<Entry<Integer, String>> entryList = new ArrayList<Entry<Integer, String>>(map.entrySet());
        Collections.sort(entryList, new Comparator<Entry<Integer, String>>() {
            public int compare(Entry<Integer, String> obj1, Entry<Integer, String> obj2) {
                String __1 = obj1.getValue();
                String __2 = obj2.getValue();
                // 문자열 vs 숫자 인경우는 ? 타입으로 바꾸어야하나 ?
                try {
                    // 숫자 비교인 경우
                    double __1_double = Double.parseDouble(__1);
                    double __2_double = Double.parseDouble(__2);
                    if (isAscending) {
                        if (__1_double > __2_double) {
                            return 1;
                        } else if (__1_double < __2_double) {
                            return -1;
                        } else {
                            return 0;
                        }
                    } else {
                        if (__1_double < __2_double) {
                            return -1;
                        } else if (__1_double < __2_double) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }

                } catch (NumberFormatException e) {
                    // 문자열인 경우
                    if (isAscending) {

                        return __1.compareTo(__2);
                    } else {
                        return __2.compareTo(__1);
                    }

                }
            }
        });

        /* 정렬된 listEnd의 인덱스를 보고 현재 테이블 수정한다 */

        for (int newIndex = 0; newIndex < this.tableRowCount; newIndex++) {
            int oldIndex = entryList.get(newIndex).getKey();

            for (int count = 0; count < this.tableData.size(); count++) {
                String __ = ti.getColumn(count).getValue(oldIndex);
                this.getColumn(count).setValue(newIndex, __);

            }

        }

        return this;
    };

    @Override
    public Table shuffle() {
        int $_last = this.tableRowCount;
        for (int $ = 0; $ < $_last; $++) {
            int rn = (int) (Math.random() * $_last);

            for (ColumnImpl ci : this.tableData) {
                // count 로우와
                // rn 로우의 값을 컬럼별로 바꿔주는

                String __ = ci.getValue($);
                ci.setValue($, ci.getValue(rn));
                ci.setValue(rn, __);
            }

        }
        return this;
    }

    public boolean fillNullWithMean() {
        boolean result = false;
        for (ColumnImpl ci : this.tableData) {
            if (ci.fillNullWithMean())
                result = true;

        }
        this.setTableInfo(this.isFirstRowHeader);
        return result;
    }

    public boolean fillNullWithZero() {
        boolean result = false;
        for (ColumnImpl ci : this.tableData) {
            if (ci.fillNullWithZero())
                result = true;

        }
        this.setTableInfo(this.isFirstRowHeader);
        return result;
    }

    @Override
    public boolean standardize() {
        boolean result = false;
        for (ColumnImpl ci : this.tableData) {
            if (ci.standardize())
                result = true;

        }
        this.setTableInfo(this.isFirstRowHeader);
        return result;
    }

    public boolean normalize() {
        boolean result = false;
        for (ColumnImpl ci : this.tableData) {
            if (ci.normalize())
                result = true;

        }
        this.setTableInfo(this.isFirstRowHeader);
        return result;
    }

    public boolean factorize() {
        boolean result = false;
        for (ColumnImpl ci : this.tableData) {
            if (ci.factorize())
                result = true;

        }
        this.setTableInfo(this.isFirstRowHeader);
        return result;
    }
}
