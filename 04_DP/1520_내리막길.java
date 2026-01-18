/*
  문제: 백준 1520번 - 내리막 길 (Gold 3)
  링크: https://www.acmicpc.net/problem/1520
  알고리즘: DP, 그래프 탐색(DFS)
  
  [암기 패턴: DFS + Memoization]
  1. 단순히 DFS로만 풀면 시간 초과가 납니다. (왔던 길을 또 계산하니까)
  2. dp[r][c] = "이 좌표(r,c)에서 도착점까지 갈 수 있는 경로의 수" 라고 정의합니다.
  3. dp 배열을 -1로 초기화합니다. (0은 '갈 수 있는 길이 없음'을 의미하므로 구분 필요)
  4. 이미 방문한 곳(dp != -1)이면 계산해둔 값을 바로 리턴합니다. (이게 핵심!)
*/

import java.io.*;
import java.util.*;

public class Main {
    static int M, N;
    static int[][] map;
    static int[][] dp;
    static int[] dr = {-1, 1, 0, 0};
    static int[] dc = {0, 0, -1, 1};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        M = Integer.parseInt(st.nextToken()); // 세로 (행)
        N = Integer.parseInt(st.nextToken()); // 가로 (열)

        map = new int[M][N];
        dp = new int[M][N];

        for (int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
                dp[i][j] = -1; // [중요] 아직 방문 안 함 표시 (-1)
            }
        }

        // (0, 0)에서 출발하여 도착점까지 가는 경로의 수 계산
        System.out.println(dfs(0, 0));
    }

    static int dfs(int r, int c) {
        // 도착점에 도달하면 경로 1개 찾음!
        if (r == M - 1 && c == N - 1) {
            return 1;
        }

        // 이미 계산해본 적 있는 좌표라면? 저장된 값(캐시) 리턴 (Memoization)
        if (dp[r][c] != -1) {
            return dp[r][c];
        }

        // 처음 방문했으니 0개부터 시작해서 카운트
        dp[r][c] = 0;

        for (int i = 0; i < 4; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];

            if (nr < 0 || nc < 0 || nr >= M || nc >= N) continue;

            // 내리막 길 조건: 현재보다 더 낮은 곳으로만 이동 가능
            if (map[nr][nc] < map[r][c]) {
                // (r,c)의 경로 수 += 다음 칸(nr,nc)에서 끝까지 가는 경로 수
                dp[r][c] += dfs(nr, nc);
            }
        }

        return dp[r][c]; // 계산 완료된 값 리턴
    }
}