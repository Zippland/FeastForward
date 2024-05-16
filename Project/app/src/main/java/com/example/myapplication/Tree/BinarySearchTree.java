package com.example.myapplication.Tree;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;

public class BinarySearchTree {
    private TreeNode root;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public void insert(String foodName, String expiryDateString, String isShared) throws ParseException {
        Date expiryDate = sdf.parse(expiryDateString);
        root = insertRec(root, foodName, expiryDate, isShared);
    }

    private TreeNode insertRec(TreeNode root, String foodName, Date expiryDate, String isShared) {
        if (root == null) {
            root = new TreeNode(foodName, expiryDate, isShared);
            return root;
        }
        if (expiryDate.compareTo(root.expiryDate) < 0) {
            root.left = insertRec(root.left, foodName, expiryDate, isShared);
        } else {
            root.right = insertRec(root.right, foodName, expiryDate, isShared);
        }
        return root;
    }

    public void traverseInOrder(Consumer<TreeNode> action) {
        traverseInOrderRec(root, action);
    }

    private void traverseInOrderRec(TreeNode node, Consumer<TreeNode> action) {
        if (node != null) {
            traverseInOrderRec(node.left, action);
            action.accept(node);
            traverseInOrderRec(node.right, action);
        }
    }
}
