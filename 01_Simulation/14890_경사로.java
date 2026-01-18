/*
  문제: 백준 14890번 - 경사로 (Gold 3)
  링크: https://www.acmicpc.net/problem/14890
  알고리즘: 구현 (Simulation)
  
  [풀이 핵심 전략]
  1. 지도의 '가로 한 줄(행)'과 '세로 한 줄(열)'을 각각 떼어내서 길을 갈 수 있는지 검사합니다.
  2. 길을 지나갈 수 있는지 판단하는 함수 `checkPath(int[] road)`를 만듭니다.
     - 높이가 같으면: 계속 전진
     - 높이 차이가 1보다 크면: 즉시 실패 (return false)
     - 낮아지는 경우 (3 -> 2): 앞으로 L칸이 평평한지, 경사로를 놓을 수 있는지 확인
     - 높아지는 경우 (2 -> 3): 뒤로 L칸이 평평한지, 이미 경사로가 없는지 확인
  3. 경사로를 겹쳐서 놓을 수 없으므로 `visited` 배열로 체크합니다.
*/

import java.io.*;
import java.util.*;

public class Main {
    static int N, L;
    static int[][] map;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken()); // 지도 크기
        L = Integer.parseInt(st.nextToken()); // 경사로 길이 (L칸 만큼 평평해야 함)
        map = new int[N][N];

        // 1. 지도 입력 받기
        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        int count = 0;

        // 2. 가로(행) 방향 검사
        for (int i = 0; i < N; i++) {
            if (checkPath(map[i])) { // i번째 행 전체를 넘김
                count++;
            }
        }

        // 3. 세로(열) 방향 검사
        for (int j = 0; j < N; j++) {
            // 세로 줄을 따로 뽑아서 배열로 만듦 (함수 재사용을 위해)
            int[] col = new int[N];
            for (int i = 0; i < N; i++) {
                col[i] = map[i][j];
            }
            
            if (checkPath(col)) {
                count++;
            }
        }

        System.out.println(count);
    }

    // 한 줄(road)이 지나갈 수 있는 길인지 확인하는 함수
    static boolean checkPath(int[] road) {
        boolean[] isSlope = new boolean[N]; // 경사로가 이미 놓였는지 체크 (겹침 방지)

        for (int i = 0; i < N - 1; i++) {
            int current = road[i];
            int next = road[i + 1];

            // 1. 높이가 같으면 패스
            if (current == next) continue;

            // 2. 높이 차이가 2 이상이면 무조건 불가능
            if (Math.abs(current - next) > 1) return false;

            // 3. 내려가는 경사로 (Current > Next) -> 앞쪽을 확인해야 함
            if (current > next) {
                // 앞으로 L칸을 확인
                for (int j = 1; j <= L; j++) {
                    // 범위 벗어나거나, 높이가 다르거나, 이미 경사로가 있으면 실패
                    if (i + j >= N || road[i + 1] != road[i + j] || isSlope[i + j]) {
                        return false;
                    }
                    isSlope[i + j] = true; // 경사로 설치 표시
                }
            }
            // 4. 올라가는 경사로 (Current < Next) -> 뒤쪽을 확인해야 함
            else {
                // 뒤로 L칸을 확인
                for (int j = 0; j < L; j++) {
                    // 범위 벗어나거나, 높이가 다르거나, 이미 경사로가 있으면 실패
                    if (i - j < 0 || road[i] != road[i - j] || isSlope[i - j]) {
                        return false;
                    }
                    isSlope[i - j] = true; // 경사로 설치 표시
                }
            }
        }
        return true; // 끝까지 무사히 통과하면 성공
    }
}