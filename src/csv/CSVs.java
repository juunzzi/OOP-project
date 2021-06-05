package csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class CSVs {
    /**
     * @param isFirstLineHeader csv 파일의 첫 라인을 헤더(타이틀)로 처리할까요?
     */
    public static Table createTable(File csv, boolean isFirstLineHeader) throws FileNotFoundException, IOException {

        TableImpl table = null;
        int lastCnt = 0;
        try {
            BufferedReader br = new BufferedReader(new BufferedReader(new FileReader(csv)));
            String line = "";
            List<ColumnImpl> li = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                int splitedLineCnt = 0;
                String[] splitedLine = line.split(",");

                if (lastCnt == 0) {
                    int idx = 0;
                    for (String sl : splitedLine) {
                        li.add(new ColumnImpl(sl, idx++, !isFirstLineHeader));
                    }

                } else {

                    for (ColumnImpl ci : li) {
                        if (splitedLineCnt == splitedLine.length) {
                            ci.columnData.add(null);
                            break;
                        }
                        String target = null;
                        if (splitedLine[splitedLineCnt].contains("\"")) {
                            target = splitedLine[splitedLineCnt++];
                            while (true) {
                                if (splitedLine[splitedLineCnt].contains("\"")) {
                                    target += splitedLine[splitedLineCnt++];
                                    break;
                                }
                                target += splitedLine[splitedLineCnt++];

                            }
                        } else {
                            target = splitedLine[splitedLineCnt++];

                        }
                        if (target == "") {
                            target = null;
                        }
                        ci.columnData.add(target);
                    }
                }
                lastCnt++;
            }
            table = new TableImpl(li);
            table.setTableInfo(!isFirstLineHeader);

        } catch (FileNotFoundException e) {
            System.out.println("파일을 찾지 못하였습니다");
        } catch (IOException e) {
            System.out.println("입출력 문제가 발생하였습니다.");
        }
        return table;
    }

    /**
     * @return 새로운 Table 객체를 반환한다. 즉, 첫 번째 매개변수 Table은 변경되지 않는다.
     */
    public static Table sort(Table table, int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {
        TableImpl tableimpl = (TableImpl) table;
        TableImpl ti = (TableImpl) tableimpl.copy();

        /* 열 인덱스 정렬 */
        ColumnImpl ci = tableimpl.tableData.get(byIndexOfColumn);
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
                    double __1_number = Double.parseDouble(__1);
                    double __2_number = Double.parseDouble(__2);
                    if (isAscending) {
                        if (__1_number > __2_number) {
                            return 1;
                        } else if (__1_number < __2_number) {
                            return -1;
                        } else {
                            return 0;
                        }
                    } else {
                        if (__1_number > __2_number) {
                            return -1;
                        } else if (__1_number < __2_number) {
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

        for (int newIndex = 0; newIndex < tableimpl.tableRowCount; newIndex++) {
            int oldIndex = entryList.get(newIndex).getKey();

            for (int count = 0; count < tableimpl.tableData.size(); count++) {
                String tmp = tableimpl.getColumn(count).getValue(oldIndex);
                ti.getColumn(count).setValue(newIndex, tmp);

            }

        }

        return ti;
    }

    /**
     * @return 새로운 Table 객체를 반환한다. 즉, 첫 번째 매개변수 Table은 변경되지 않는다.
     */
    public static Table shuffle(Table table) {
        // 원본테이블을 복사해준다.
        TableImpl ti = (TableImpl) table;
        TableImpl newTable = (TableImpl) ti.copy();

        int $_last = newTable.tableRowCount;
        for (int $ = 0; $ < $_last; $++) {

            // 랜덤한수를 받아옵니다.
            int rn = (int) (Math.random() * $_last);

            for (ColumnImpl ci : newTable.tableData) {
                // $ 로우와
                // rn 로우의 값을 컬럼별로 바꿔주는

                String tmp = ci.getValue($);
                ci.setValue($, ci.getValue(rn));
                ci.setValue(rn, tmp);
            }

        }
        return newTable;
    }

}
