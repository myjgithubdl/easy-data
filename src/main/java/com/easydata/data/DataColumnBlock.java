package com.easydata.data;

import com.easydata.head.TheadColumnTree;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by MYJ on 2018/1/30.
 */
@Getter
@Setter
public class DataColumnBlock {

    private TheadColumnTree theadColumnTree ;

    private int startRowIndex;

    private int endRowIndex;

    public DataColumnBlock() {
    }

    public DataColumnBlock(TheadColumnTree theadColumnTree , int startRowIndex ,int endRowIndex  ) {
        this.theadColumnTree=theadColumnTree;
        this.startRowIndex=startRowIndex;
        this.endRowIndex=endRowIndex;
    }

}
