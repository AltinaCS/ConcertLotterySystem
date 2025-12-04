package org.example.concertlotterysystem.entities;

import org.example.concertlotterysystem.repository.LotteryDrawerDAO;
import org.example.concertlotterysystem.repository.LotteryEntryDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LotteryDrawer {
    private List<LotteryEntry> lotteryEntryList;
    private int quota;

    private final LotteryDrawerDAO lotteryDrawerDAO = new LotteryDrawerDAO();
    private static final Random random = new Random();

    public LotteryDrawer(Event event){
        lotteryEntryList = lotteryDrawerDAO.getLotteryEntry(event);
        this.quota = event.getQuota();
    }

    public void runLottery(){
        List<LotteryEntry> winList = new ArrayList<>();
        List<LotteryEntry> loseList = new ArrayList<>();
        LotteryEntryDAO lotteryEntryDAO = new LotteryEntryDAO();
        if(lotteryEntryList.size() <= quota){
            winList = lotteryEntryList;

        }else{
            for (int i = 0; i < quota; i++){
                int winIndex = random.nextInt(lotteryEntryList.size());
                winList.add(lotteryEntryList.get(winIndex));
                lotteryEntryList.remove(winIndex);
            }
            loseList = lotteryEntryList;
        }

        for (LotteryEntry entry : winList){
            entry.setStatus(LotteryEntryStatus.WON);
        }

        for (LotteryEntry entry : loseList){
            entry.setStatus(LotteryEntryStatus.LOST);
        }
        List<LotteryEntry> allUpdates = new ArrayList<>();
        allUpdates.addAll(winList);
        allUpdates.addAll(loseList);

        if (!allUpdates.isEmpty()) {
            lotteryEntryDAO.updateStatusBatch(allUpdates);
        }
    }
}
