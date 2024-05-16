package com.example.myapplication.Tree;

import java.util.Date;

public class TreeNode {
    public String foodName;
    public Date expiryDate;
    public String isShared;
    TreeNode left, right;

    TreeNode(String foodName, Date expiryDate, String isShared) {
        this.foodName = foodName;
        this.expiryDate = expiryDate;
        this.isShared = isShared;
        this.left = this.right = null;
    }
}
