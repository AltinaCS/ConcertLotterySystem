package org.example.concertlotterysystem.entities;

public enum EventStatus {
    DRAFT,              // 已建活動但尚未開放報名
    OPEN,  // 開放會員報名/抽籤登記
    CLOSED,             // 截止報名，等待或準備開獎
    DRAWN,              // 已開獎
    CANCELLED           // 活動取消或已結束
}
