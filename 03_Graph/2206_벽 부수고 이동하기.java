/*
  문제: 백준 2206번 - 벽 부수고 이동하기 (Gold 3)
  링크: https://www.acmicpc.net/problem/2206
  알고리즘: 그래프 탐색 (BFS)
  
  [암기 패턴: 3차원 방문 배열]
  1. 일반적인 최단 거리는 visited[row][col] (2차원)입니다.
  2. 하지만 "특별한 능력(벽 부수기)"이 있다면 상태를 구분해야 합니다.
     - visited[row][col][0]: 벽을 안 부수고 도달했음 (능력 보유)
     - visited[row][col][1]: 벽을 이미 부수고 도달했음 (능력 사용함)
     
  3. 큐(Queue)에 넣을 때도 좌표(r, c)뿐만 아니라,
     "현재 이동 거리(cnt)"와 "벽을 부쉈는지 여부(destroyed)"를 함께 들고 다녀야 합니다.
*/

import java.io.*;
import java.util.*;

public class Main {
    // 큐에 넣을 상태 클래스
    static class Node {
        int r, c;       // 좌표
        int cnt;        // 이동 거리
        int destroyed;  // 벽 파괴 여부 (0: 안 부숨, 1: 부숨)

        public Node(int r, int c, int cnt, int destroyed) {
            this.r = r;
            this.c = c;
            this.cnt = cnt;
            this.destroyed = destroyed;
        }
    }

    static int N, M;
    static int[][] map;
    // [행][열][파괴여부] -> 0: 안 부숨, 1: 부숨
    static boolean[][][] visited;
    
    static int[] dr = {-1, 1, 0, 0};
    static int[] dc = {0, 0, -1, 1};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        map = new int[N][M];
        visited = new boolean[N][M][2]; // [중요] 3차원 배열 선언

        for (int i = 0; i < N; i++) {
            String str = br.readLine();
            for (int j = 0; j < M; j++) {
                // 붙어서 들어오는 입력(01001..)은 charAt - '0' 으로 처리
                map[i][j] = str.charAt(j) - '0';
            }
        }

        System.out.println(bfs());
    }

    static int bfs() {
        Queue<Node> q = new LinkedList<>();
        
        // 시작점 (0, 0), 거리 1, 파괴안함(0)
        q.offer(new Node(0, 0, 1, 0));
        visited[0][0][0] = true;

        while (!q.isEmpty()) {
            Node current = q.poll();

            // 목적지 도착! BFS는 가장 먼저 도착한 놈이 무조건 최단 거리임
            if (current.r == N - 1 && current.c == M - 1) {
                return current.cnt;
            }

            for (int i = 0; i < 4; i++) {
                int nr = current.r + dr[i];
                int nc = current.c + dc[i];

                // 맵 범위 체크
                if (nr < 0 || nc < 0 || nr >= N || nc >= M) continue;

                // Case 1: 다음 칸이 '벽(1)'인 경우
                if (map[nr][nc] == 1) {
                    // 내가 아직 벽을 부순 적이 없다면(0)? -> 부수고 간다!
                    if (current.destroyed == 0 && !visited[nr][nc][1]) {
                        visited[nr][nc][1] = true; // 부신 상태로 방문 체크
                        q.offer(new Node(nr, nc, current.cnt + 1, 1)); // 상태를 1로 변경
                    }
                }
                // Case 2: 다음 칸이 '빈 칸(0)'인 경우
                else {
                    // 그냥 이동하면 됨. 단, 내 현재 상태(부쉈는지 여부) 그대로 방문 체크
                    if (!visited[nr][nc][current.destroyed]) {
                        visited[nr][nc][current.destroyed] = true;
                        q.offer(new Node(nr, nc, current.cnt + 1, current.destroyed));
                    }
                }
            }
        }

        return -1; // 도착 못 함
    }
}