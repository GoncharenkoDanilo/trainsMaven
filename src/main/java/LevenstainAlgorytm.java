/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Данил
 */
public class LevenstainAlgorytm {

    List<String> result;

    public LevenstainAlgorytm() {
        result = new ArrayList<String>();
    }

    public List<String> compareAll(List<String> data, String key, int numOfOptions) {

        int[] nums = new int[data.size()];

        for (int i = 0; i < data.size(); i++) {
            nums[i] = compare(data.get(i), key);
        }
        
        bubbleSortModify(nums, data);
        
        List<String> elemsForRemove = new ArrayList<String>();
        for (int i = numOfOptions; i < data.size(); i++)
            elemsForRemove.add(data.get(i));
        
        data.removeAll(elemsForRemove);

        return data;
    }

    private void bubbleSortModify(int[] arr, List<String> data) {
        for (int i = arr.length - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j];
                    String txt = data.get(j);

                    arr[j] = arr[j + 1];
                    data.set(j, data.get(j + 1));

                    arr[j + 1] = tmp;
                    data.set(j + 1, txt);
                }
            }
        }
    }

    private int compare(String str1, String str2) {
        int[] Di_1 = new int[str2.length() + 1];
        int[] Di = new int[str2.length() + 1];

        for (int j = 0; j <= str2.length(); j++) {
            Di[j] = j; // (i == 0)
        }

        for (int i = 1; i <= str1.length(); i++) {
            System.arraycopy(Di, 0, Di_1, 0, Di_1.length);

            Di[0] = i; // (j == 0)
            for (int j = 1; j <= str2.length(); j++) {
                int cost = (str1.charAt(i - 1) != str2.charAt(j - 1)) ? 1 : 0;
                Di[j] = min(
                        Di_1[j] + 1,
                        Di[j - 1] + 1,
                        Di_1[j - 1] + cost
                );
            }
        }

        return Di[Di.length - 1];
    }

    private int min(int n1, int n2, int n3) {
        return Math.min(Math.min(n1, n2), n3);
    }
}
