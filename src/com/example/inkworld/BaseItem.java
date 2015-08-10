package com.example.inkworld;

import java.io.Serializable;

public class BaseItem implements Serializable {
    private int item_type = 0;
    public BaseItem(int item_type) {
      this.item_type = item_type;
    }
    public int getItem_type() {
      return item_type;
    }
    public void setItem_type(int item_type) {
      this.item_type = item_type;
    }
  }
