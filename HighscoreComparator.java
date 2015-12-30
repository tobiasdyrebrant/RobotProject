package tobdyh131;

import java.util.Comparator;

/**
 * Created by Tobias on 30-Dec-15.
 */
public class HighscoreComparator implements Comparator<HighscoreInfo> {
    @Override
    public int compare(HighscoreInfo o1, HighscoreInfo o2) {
        int score1 = o1.score;
        int score2 = o2.score;

        if(score1 > score2)
        {
            return -1;
        }
        else if(score1 < score2)
        {
            return +1;
        }
        else
        {
            return 0;
        }
    }
}
