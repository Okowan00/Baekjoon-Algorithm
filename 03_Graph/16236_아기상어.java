/*
  문제: 백준 16236번 - 아기 상어 (Gold 3)
  링크: https://www.acmicpc.net/problem/16236
  알고리즘: 그래프 탐색(BFS), 구현
  
  [암기 패턴: 조건부 BFS]
  1. "가장 가까운 먹이"를 찾아야 하므로 BFS를 씁니다.
  2. 먹을 수 있는 물고기가 여러 마리라면 "가장 위쪽, 가장 왼쪽"을 골라야 합니다.
     -> 단순히 큐(Queue)에 넣으면 안 되고, 후보를 리스트에 담아 정렬(Sort)해야 합니다.
  3. 상어의 크기(size)와 먹은 횟수(eatCnt)를 관리하며 시뮬레이션 돌립니다.
*/

import java.io.*;
import java.util.*;

public class Main {
    static int N;
    static int[][] map;
    static int sharkR, sharkC, sharkSize = 2, eatCnt = 0; // 상어 상태
    static int ans = 0; // 총 걸린 시간

    // 상좌우하 (위쪽, 왼쪽 우선순위를 위해 상/좌를 먼저 배치해도 되지만, 정렬로 해결함)
    static int[] dr = {-1, 0, 1, 0};
    static int[] dc = {0, -1, 0, 1};

    static class Node implements Comparable<Node> {
        int r, c, dist;
        public Node(int r, int c, int dist) {
            this.r = r;
            this.c = c;
            this.dist = dist;
        }
        // 정렬 기준: 1. 거리 짧은 순 2. 가장 위쪽(r 작은 순) 3. 가장 왼쪽(c 작은 순)
        @Override
        public int compareTo(Node o) {
            if (this.dist != o.dist) return this.dist - o.dist;
            if (this.r != o.r) return this.r - o.r;
            return this.c - o.c;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        N = Integer.parseInt(br.readLine());
        map = new int[N][N];

        for (int i = 0; i < N; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < N; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
                if (map[i][j] == 9) { // 아기 상어 위치 저장
                    sharkR = i;
                    sharkC = j;
                    map[i][j] = 0; // 상어가 있던 곳은 빈 칸으로 변경
                }
            }
        }

        while (true) {
            // 먹을 수 있는 물고기를 찾으러 BFS 탐색
            Node fish = bfs();

            // 더 이상 먹을 물고기가 없으면 종료 (엄마 상어 부름)
            if (fish == null) break;

            // 물고기 먹기 처리
            sharkR = fish.r;
            sharkC = fish.c;
            ans += fish.dist; // 이동 거리만큼 시간 추가
            map[sharkR][sharkC] = 0; // 냠냠
            eatCnt++;

            // 자신의 크기만큼 먹었으면 크기 +1
            if (eatCnt == sharkSize) {
                sharkSize++;
                eatCnt = 0;
            }
        }
        System.out.println(ans);
    }

    // 현재 위치에서 먹을 수 있는 가장 가까운 물고기 찾기
    static Node bfs() {
        // 거리순 -> 위쪽 -> 왼쪽 순으로 자동 정렬되는 우선순위 큐 사용
        PriorityQueue<Node> pq = new PriorityQueue<>();
        boolean[][] visited = new boolean[N][N];

        // 시작점(현재 상어 위치) 넣기 (거리는 0)
        pq.offer(new Node(sharkR, sharkC, 0));
        visited[sharkR][sharkC] = true;

        while (!pq.isEmpty()) {
            Node now = pq.poll();

            // [중요] 먹을 수 있는 물고기 발견? (빈 칸 아니고, 나보다 작아야 함)
            if (map[now.r][now.c] != 0 && map[now.r][now.c] < sharkSize) {
                return now; // 우선순위 큐라서 가장 먼저 나온 놈이 정답
            }

            for (int i = 0; i < 4; i++) {
                int nr = now.r + dr[i];
                int nc = now.c + dc[i];

                if (nr < 0 || nc < 0 || nr >= N || nc >= N) continue;
                if (visited[nr][nc]) continue;
                
                // 이동 가능 조건: 빈 칸이거나, 나랑 크기가 같거나 작아야 함
                if (map[nr][nc] <= sharkSize) {
                    visited[nr][nc] = true;
                    pq.offer(new Node(nr, nc, now.dist + 1));
                }
            }
        }
        return null; // 먹을 게 없음
    }
}