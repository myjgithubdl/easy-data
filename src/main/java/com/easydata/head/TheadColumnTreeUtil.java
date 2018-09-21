package com.easydata.head;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by MYJ on 2017/6/9.
 */
public class TheadColumnTreeUtil {



    public void show(List<TheadColumnTree> list){
        for(TheadColumnTree t:list){
            System.out.println("标题:"+t.getText()+"-->Field:"+t.getName()+"-->Col:"+t.getColspan()+"-->Row:"+t.getRowspan()+"-->celIndex:"+t.getCelIndex()+"-->celDepth:"+t.getDepth());
            if(t.getChildTheadColumnTrees()!=null){
                show(t.getChildTheadColumnTrees());
            }
        }
    }

    /**
     * 将List转化为table、主要计算跨行、跨列、列索引
     * @param tableHeads
     * @return
     */
    public List<List<TheadColumnTree>> getFromtTheadColumnTreeList(List<TheadColumnTree> tableHeads){
        fromtTheadColumnTree(tableHeads);
        //show(tableHeads);
        List<List<TheadColumnTree>> objects=new ArrayList<List<TheadColumnTree>>();
        formatObjectArray(objects,tableHeads);
        depth=1;
        nowIndex=0;
        return objects;
    }



   /* public List<List<TheadColumnTree>> getFromtTheadColumnTreeList(List<TheadColumnTree> tableHeads,boolean isObtainHiddenColume){
        List<List<TheadColumnTree>> result;
        //不包含隐藏列
        if(!isObtainHiddenColume){
            result=this.getFromtTheadColumnTreeList(this.removeHiddenColume(tableHeads));
        }else{
            result=this.getFromtTheadColumnTreeList(tableHeads);
        }
        return result;
    }*/

    /**
     * 将List转化为table、主要计算跨行、跨列、列索引
     * @param theadColumnList
     * @param isObtainHiddenColume  是否包含隐藏列
     * @return
     */
    public static List<List<TheadColumnTree>> getFromtTheadColumnTreeList(List<TheadColumn> theadColumnList ,boolean isObtainHiddenColume){
        //转换为TheadColumnTree
        List<TheadColumnTree> theadColumnTreeList = new ArrayList<TheadColumnTree>();
        for(TheadColumn theadColumn : theadColumnList){
            TheadColumnTree theadColumnTree =new TheadColumnTree();
            try {
                BeanUtils.copyProperties(theadColumnTree ,theadColumn );
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            theadColumnTreeList.add(theadColumnTree);
        }
        TheadColumnTreeUtil theadColumnTreeUtil = new TheadColumnTreeUtil();
        List<List<TheadColumnTree>> result;
        //不包含隐藏列
        if(!isObtainHiddenColume){
            result=theadColumnTreeUtil.getFromtTheadColumnTreeList(theadColumnTreeUtil.removeHiddenColume(theadColumnTreeList));
        }else{
            result=theadColumnTreeUtil.getFromtTheadColumnTreeList(theadColumnTreeList);
        }
        return result;
    }

    public List<TheadColumnTree> removeHiddenColume(List<TheadColumnTree> tableHeads){
        List<TheadColumnTree> result = new ArrayList<>();
        for(TheadColumnTree theadColumnTree : tableHeads){
            //不是隐藏列
            if(!theadColumnTree.isHidden()){
                result.add(theadColumnTree);
            }else{//隐藏列，设置所有孩子都为隐藏
                String id=theadColumnTree.getId();
                for(TheadColumnTree theadColumnTree2 : tableHeads ){
                    if(id.equals(theadColumnTree2.getPid())){
                        System.out.println(id+"的孩子"+theadColumnTree2.getId()+"被隐藏");
                        theadColumnTree2.setHidden(true);
                    }
                }
            }
        }
        return result;
    }






    public List<List<TheadColumnTree>> getFromtTableRowList(List<TheadColumnTree> tableHeads){
        List<List<TheadColumnTree>> tableHeadList=getFromtTheadColumnTreeList(tableHeads);
        Map<String, Integer> fieldIndex=getFieldCelIndex(tableHeadList);
        List<List<TheadColumnTree>> tableRowList=new ArrayList<List<TheadColumnTree>>();
        boolean falg=true;
        for(List<TheadColumnTree> heads:tableHeadList){
            for(TheadColumnTree head:heads){
                if(falg){
                    for(int i=0;i<head.getColspan();i++){
                        List<TheadColumnTree> row=new ArrayList<TheadColumnTree>();
                        tableRowList.add(row);
                    }
                }
                tableRowList.get(fieldIndex.get(head.getName())).add(head.unitSwap());
            }
            falg=false;
        }
        return tableRowList;
    }
    /**
     * 格式层级对应关系
     * @param tableHeads
     */
    public void fromtTheadColumnTree(List<TheadColumnTree> tableHeads){
        relevanceTheadColumnTree(tableHeads,null);
        downCol(tableHeads);
        setDepth(tableHeads);
        downRow(tableHeads);
        finalRowFormat(tableHeads);
        initFieldIsCelIndex(tableHeads);
    }
    /**
     * 向下寻找子级，如果有子级，就设置本级的跨列数，并且要通知父级又叠加上本级的跨列数
     * @param list
     */
    public void downCol(List<TheadColumnTree> list){
        for(TheadColumnTree t:list){
            if(t.getChildTheadColumnTrees()!=null){
                t.setColspan(t.getChildTheadColumnTrees().size());
                upCol(t,t.getChildTheadColumnTrees().size()-1);
                downCol(t.getChildTheadColumnTrees());
            }
        }
    }
    /**
     * 向上寻找父级，如果有父级，就叠加跨列数
     * @param tableHead
     * @param colspan 跨列数
     */
    public void upCol(TheadColumnTree tableHead,Integer colspan){
        if(tableHead.getParentTheadColumnTree()!=null){
            tableHead.getParentTheadColumnTree().setColspan(tableHead.getParentTheadColumnTree().getColspan()+colspan);
            upCol(tableHead.getParentTheadColumnTree(),colspan);
        }
    }
    private int depth=1;
    /**
     * 向下寻找子级，如果没有子级，就设置本级的跨行数，跨行数=等级最大深度-本级深度+1
     * @param list
     */
    public void downRow(List<TheadColumnTree> list){
        for(TheadColumnTree t:list){
            if(t.getChildTheadColumnTrees()!=null){
                downRow(t.getChildTheadColumnTrees());
            }else{
                int r=depth-Integer.valueOf(t.getDepth())+1;
                t.setRowspan(r);
            }
        }
    }
    /**
     * 寻找最终子级，当最终子级的父级跨列数为1时，重新设置跨行数和最终子级最大层级数，最终子级跨行数=1、最终子级的父级跨行数=最终子级的父级跨行数+最终子级跨行数-1
     * @param list
     */
    public void finalRowFormat(List<TheadColumnTree> list){
        for(TheadColumnTree t:list){
            if(t.getChildTheadColumnTrees()!=null&&t.getChildTheadColumnTrees().size()>0){
                finalRowFormat(t.getChildTheadColumnTrees());
            }else{
                if(t.getParentTheadColumnTree()!=null&&t.getParentTheadColumnTree().getColspan()==1){
                    t.getParentTheadColumnTree().setRowspan(t.getParentTheadColumnTree().getRowspan()+t.getRowspan()-1);
                    t.setRowspan(1);
                    t.setDepth(depth);
                }
            }
        }
    }
    /**
     * 重设等级深度，并且计算出最大等级深度
     * @param list
     */
    public void setDepth(List<TheadColumnTree> list){
        for(TheadColumnTree t:list){
            if(t.getParentTheadColumnTree()==null){
                t.setDepth(1);
            }else{
                int d=Integer.valueOf(t.getParentTheadColumnTree().getDepth())+1;
                t.setDepth(d);
                if(d>=depth){
                    depth=d;
                }
            }
            if(t.getChildTheadColumnTrees()!=null){
                setDepth(t.getChildTheadColumnTrees());
                //t.setField(null);
                //t.setWidth("");
            }
        }
    }
    /**
     * 格式化对象，格式成每行对映每列的集合对象
     * @param objects 保存格式化数据的对象
     * @param list
     */
    public void formatObjectArray(List<List<TheadColumnTree>> objects,List<TheadColumnTree> list){
        if(objects.size()==0){
            for(int i= 0;i<depth;i++){
                objects.add(new ArrayList<TheadColumnTree>());
            }
        }
        for(TheadColumnTree t:list){
            if(t.getChildTheadColumnTrees()!=null){
                formatObjectArray(objects,t.getChildTheadColumnTrees());
            }
            int r=Integer.valueOf(t.getDepth())-1;
            objects.get(r).add(t);
            t.setParentTheadColumnTree(null);
            t.setChildTheadColumnTrees(null);
        }
    }
    /**
     * 寻找集合中父子级的关系，并且把子级的对象加到父级中
     * @param list
     * @param tableHead
     */
    public void relevanceTheadColumnTree(List<TheadColumnTree> list,TheadColumnTree tableHead){
        List<TheadColumnTree> ths=new ArrayList<TheadColumnTree>();
        if(tableHead==null){
            for(TheadColumnTree t:list){
                if(t.getPid()==null || "".equals(t.getPid().trim()) || "0".equals(t.getPid().trim())){
                    ths.add(t);
                }
            }
        }else{
            for(int i=list.size()-1;i>=0;i--){
                if(tableHead.getId().equals(list.get(i).getPid())){
                    if(tableHead.getChildTheadColumnTrees()==null){
                        tableHead.setChildTheadColumnTrees(new ArrayList<TheadColumnTree>());
                        tableHead.setHasChild("Y");
                    }
                    tableHead.getChildTheadColumnTrees().add(0,list.get(i));
                    list.get(i).setParentTheadColumnTree(tableHead);
                    ths.add(list.get(i));
                    list.remove(i);
                }
            }
        }
        for(TheadColumnTree t:ths){
            relevanceTheadColumnTree(list,t);
        }
    }
    private int nowIndex=0;
    /**
     * 向下寻找子级，如果没有子级，就设置本级列索引
     * @param list
     */
    public void initFieldIsCelIndex(List<TheadColumnTree> list){
        for(TheadColumnTree t:list){
            if(t.getChildTheadColumnTrees()==null){
                t.setCelIndex(nowIndex);
                nowIndex+=1;
            }else{
                t.setCelIndex(nowIndex);
                initFieldIsCelIndex(t.getChildTheadColumnTrees());
            }
        }
    }
    /**
     * 获取field对映列索引的集合
     * @param objects 格式化数据的对象
     * @return 返回field对映列索引的集合
     */
    public Map<String, Integer> getFieldCelIndex(List<List<TheadColumnTree>> objects){
        Map<String, Integer> map=new LinkedHashMap<String, Integer>();
        for(List<TheadColumnTree> list:objects){
            for(TheadColumnTree t:list){
                if(t.getCelIndex()!=null){
                    map.put(t.getName(), t.getCelIndex());
                }
            }
        }
        return map;
    }

    /**
     * 将跨行跨列的转换为不跨行跨列的表头，即跨行跨列的元数据列相同（目前适用于导出CSV 文件的表头）
     * @param list
     * @return
     */
    public static List<List<TheadColumnTree>> getNoRowspanAndNoColspanTableHead(List<List<TheadColumnTree>> list) {
        List<List<TheadColumnTree>> resultList = null;
        //最大的列索引号,从0开始
        int maxCellIndex = 0;
        if (list != null && list.size() > 0) {
            resultList = new ArrayList<>(list.size());

            //用第一行计算最大的列数
            List<TheadColumnTree> tableHeadMetaDataColumnFirstTr = list.get(0);
            for(TheadColumnTree tableHeadMetaDataColumnTh : tableHeadMetaDataColumnFirstTr) {
                int colspan = tableHeadMetaDataColumnTh.getColspan();
                maxCellIndex = maxCellIndex + colspan;
            }
            maxCellIndex--;

            //初始化所有行、列，容易对跨行的列操作
            for(int m = 0, n = list.size(); m < n; m++){
                List<TheadColumnTree> tableHeadMetaDataColumnTr=new ArrayList<>();
                for(int i=0 ; i<=maxCellIndex;i++ ){
                    TheadColumnTree tableHeadMetaDataColumnTh=new TheadColumnTree();
                    tableHeadMetaDataColumnTr.add(tableHeadMetaDataColumnTh);
                }
                resultList.add(tableHeadMetaDataColumnTr);
            }

            for (int m = 0, n = list.size(); m < n; m++) {
                List<TheadColumnTree> tableHeadMetaDataColumnTr = list.get(m);
                if (tableHeadMetaDataColumnTr != null && tableHeadMetaDataColumnTr.size() > 0) {
                    int newCelIndex=0;
                    for(int i = 0, j = tableHeadMetaDataColumnTr.size(); i < j; i++){
                        TheadColumnTree tableHeadMetaDataColumnTh=tableHeadMetaDataColumnTr.get(i);
                        int rowspan = tableHeadMetaDataColumnTh.getRowspan();//跨行数
                        int colspan = tableHeadMetaDataColumnTh.getColspan();//跨列数
                        int celIndex = tableHeadMetaDataColumnTh.getCelIndex();//列索引
                        newCelIndex=celIndex;
                        //单元格要么跨行、要么跨列
                        if( (rowspan ==1 && colspan ==1) ||  (rowspan ==1 && colspan >1) ){//不跨行不跨列  不跨行跨列
                            for(int colspanIndex=0 ; colspanIndex<colspan;colspanIndex++){
                                TheadColumnTree newTableHeadMetaDataColumnTh=resultList.get(m).get(newCelIndex);
                                copyProperties(newTableHeadMetaDataColumnTh , tableHeadMetaDataColumnTh );
                                newTableHeadMetaDataColumnTh.setDepth(m);
                                newTableHeadMetaDataColumnTh.setRowspan(1);
                                newTableHeadMetaDataColumnTh.setColspan(1);
                                newTableHeadMetaDataColumnTh.setCelIndex(newCelIndex);
                                resultList.get(m).set(newCelIndex , newTableHeadMetaDataColumnTh);
                                newCelIndex++;
                            }

                        }else if(rowspan >1 && colspan ==1  ){//跨行不跨列
                            for(int rowspanIndex=0 ; rowspanIndex<rowspan;rowspanIndex++){
                                TheadColumnTree newTableHeadMetaDataColumnTh=resultList.get(m+rowspanIndex).get(newCelIndex);
                                copyProperties(newTableHeadMetaDataColumnTh , tableHeadMetaDataColumnTh );
                                newTableHeadMetaDataColumnTh.setDepth(m);
                                newTableHeadMetaDataColumnTh.setRowspan(1);
                                newTableHeadMetaDataColumnTh.setColspan(1);
                                newTableHeadMetaDataColumnTh.setCelIndex(newCelIndex);
                                resultList.get(m+rowspanIndex).set(newCelIndex , newTableHeadMetaDataColumnTh);
                            }
                            newCelIndex++;
                        }
                    }
                }
            }
        }
        return resultList;
    }

    /**
     * 拷贝属性
     * @param dest
     * @param orig
     */
    public static void copyProperties(Object dest, Object orig){
        try {
            BeanUtils.copyProperties(dest , orig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取叶子节点、即没有孩子节点的列
     * @param theadColumnTrees
     * @return
     */
    public static List<TheadColumnTree> getLeafNodes(List<List<TheadColumnTree>> theadColumnTrees){
        if(theadColumnTrees == null || theadColumnTrees.size() < 1) return null;
        List<TheadColumnTree> resultList= new ArrayList<>();
        for( List<TheadColumnTree> theadColumnTree2 : theadColumnTrees){
            for(TheadColumnTree theadColumnTree : theadColumnTree2){
                if("N".equals(theadColumnTree.getHasChild())){
                    resultList.add(theadColumnTree);
                }
            }
        }

        Collections.sort(resultList, new Comparator<TheadColumnTree>() {
            @Override
            public int compare(TheadColumnTree o1, TheadColumnTree o2) {
                return o1.getCelIndex().compareTo(o2.getCelIndex());
            }
        });

        return resultList;
    }


    public static void main(String[] args) {
        List attachOneList = new ArrayList();
        TheadColumnTree theadColumnTree =new TheadColumnTree("11", null, "field1","标题1" );
        attachOneList.add(theadColumnTree);
        theadColumnTree =new TheadColumnTree("12", null, "field2","标题2" );
        theadColumnTree.setHidden(true);
        attachOneList.add(theadColumnTree);
        attachOneList.add(new TheadColumnTree("13", null, "field3","标题3" ));

        attachOneList.add(new TheadColumnTree("21", "12", "field21","标题21" ));
        attachOneList.add(new TheadColumnTree("22", "12", "field22","标题22" ));
        attachOneList.add(new TheadColumnTree("23", "12", "field23","标题23" ));
        attachOneList.add(new TheadColumnTree("24", "13", "field24","标题24" ));
        TheadColumnTreeUtil tableUtil = new TheadColumnTreeUtil();

        /*List<List<TheadColumnTree>> headTableList2 = tableUtil.getFromtTheadColumnTreeList(attachOneList,false);


        Object object2 = JSONObject.toJSON(headTableList2);

        System.out.println("===========================");
        System.out.println(object2);
        System.out.println("===========================");
        System.out.println(JSONObject.toJSON(tableUtil.getNoRowspanAndNoColspanTableHead(headTableList2)));
    */
    }



}
