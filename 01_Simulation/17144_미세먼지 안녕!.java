/*
  문제: 백준 17144번 - 미세먼지 안녕! (Gold 4)
  링크: https://www.acmicpc.net/problem/17144
  알고리즘: 구현, 시뮬레이션
  
  [암기 패턴: 동시성 확산 & 배열 회전]
  1. 확산(Spread): 모든 먼지가 '동시에' 확산됩니다.
     - 원본 배열(map)에 바로 더하면, 방금 확산된 먼지가 또 확산되는 문제가 생깁니다.
     - 반드시 '임시 배열(newMap)'에 확산 결과를 기록하고, 마지막에 원본에 덮어씌워야 합니다.
  
  2. 공기청정기(Clean): 배열의 값을 한 칸씩 미는(Rotate) 작업입니다.
     - 바람이 부는 방향으로 값을 밀어내면 데이터가 덮어씌워져 사라집니다.
     - 바람의 '반대 방향'에서 값을 '당겨오는(Pull)' 방식으로 구현해야 합니다.
     - 반시계(위쪽): ↓ ← ↑ → 순서로 당기기
     - 시계(아래쪽): ↑ ← ↓ → 순서로 당기기
*/

import java.io.*;
import java.util.*;

public class Main {
    static int R, C, T;
    static int[][] map;
    static int cleanerTop = -1; // 위쪽 공기청정기 행 위치 (아래쪽은 +1)
    
    // 방향: 우, 상, 좌, 하 (반시계/시계 방향 구현 시 헷갈리지 않게 주의)
    static int[] dr = {0, -1, 0, 1};
    static int[] dc = {1, 0, -1, 0};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());

        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        T = Integer.parseInt(st.nextToken());

        map = new int[R][C];

        for (int i = 0; i < R; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < C; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());

                // 공기청정기 위치 찾기 (-1)
                // 위쪽 공기청정기를 먼저 발견하면 저장
                if (map[i][j] == -1 && cleanerTop == -1) {
                    cleanerTop = i;
                }
            }
        }

        // T초 동안 시뮬레이션 반복
        while (T-- > 0) {
            spread(); // 1. 미세먼지 확산
            clean();  // 2. 공기청정기 작동
        }

        // 남은 미세먼지 총합 계산
        System.out.println(countDust());
    }

    static void spread() {
        // [중요] 동시성 처리를 위한 임시 배열 생성
        int[][] newMap = new int[R][C];

        for (int i = 0; i < R; i++) {
            for (int j = 0; j < C; j++) {
                // 공기청정기는 그대로 복사
                if (map[i][j] == -1) {
                    newMap[i][j] = -1;
                    continue;
                }

                // 먼지가 있는 경우 확산
                if (map[i][j] > 0) {
                    int amount = map[i][j] / 5; // 확산되는 양
                    int cnt = 0; // 확산된 방향 개수

                    for (int d = 0; d < 4; d++) {
                        int nr = i + dr[d];
                        int nc = j + dc[d];

                        // 맵 범위 안이고, 공기청정기가 아니면 확산 가능
                        if (nr >= 0 && nr < R && nc >= 0 && nc < C && map[nr][nc] != -1) {
                            newMap[nr][nc] += amount; // 다른 곳에서 온 먼지와 누적됨
                            cnt++;
                        }
                    }
                    
                    // 남은 미세먼지 = 원래 양 - (확산량 * 방향 개수)
                    // [중요] 내 자리에 남은 먼지도 newMap에 더해줘야 함
                    newMap[i][j] += map[i][j] - (amount * cnt);
                }
            }
        }

        // 확산이 끝난 임시 맵을 원본 맵에 반영
        map = newMap;
    }

    static void clean() {
        int top = cleanerTop;       // 위쪽 청정기 (반시계)
        int down = cleanerTop + 1;  // 아래쪽 청정기 (시계)

        // 1. 위쪽 청정기 작동 (반시계: 오른쪽 -> 위 -> 왼쪽 -> 아래 순으로 바람)
        // 값 이동은 역순(당겨오기): ↓ ← ↑ →
        
        // 1-1. 왼쪽 벽 (위에서 아래로 당기기 ↓)
        for (int i = top - 1; i > 0; i--) map[i][0] = map[i - 1][0];

        // 1-2. 위쪽 벽 (오른쪽에서 왼쪽으로 당기기 ←)
        for (int i = 0; i < C - 1; i++) map[0][i] = map[0][i + 1];

        // 1-3. 오른쪽 벽 (아래에서 위로 당기기 ↑)
        for (int i = 0; i < top; i++) map[i][C - 1] = map[i + 1][C - 1];

        // 1-4. 가운데 벽 (왼쪽에서 오른쪽으로 당기기 →)
        for (int i = C - 1; i > 1; i--) map[top][i] = map[top][i - 1];

        // 청정기에서 바로 나온 바람은 깨끗함
        map[top][1] = 0;


        // 2. 아래쪽 청정기 작동 (시계: 오른쪽 -> 아래 -> 왼쪽 -> 위 순으로 바람)
        // 값 이동은 역순(당겨오기): ↑ ← ↓ →

        // 2-1. 왼쪽 벽 (아래에서 위로 당기기 ↑)
        for (int i = down + 1; i < R - 1; i++) map[i][0] = map[i + 1][0];

        // 2-2. 아래쪽 벽 (오른쪽에서 왼쪽으로 당기기 ←)
        for (int i = 0; i < C - 1; i++) map[R - 1][i] = map[R - 1][i + 1];

        // 2-3. 오른쪽 벽 (위에서 아래로 당기기 ↓)
        for (int i = R - 1; i > down; i--) map[i][C - 1] = map[i - 1][C - 1];

        // 2-4. 가운데 벽 (왼쪽에서 오른쪽으로 당기기 →)
        for (int i = C - 1; i > 1; i--) map[down][i] = map[down][i - 1];

        // 청정기에서 바로 나온 바람은 깨끗함
        map[down][1] = 0;
    }

    static int countDust() {
        int sum = 0;
        for (int i = 0; i < R; i++) {
            for (int j = 0; j < C; j++) {
                if (map[i][j] != -1) {
                    sum += map[i][j];
                }
            }
        }
        return sum;
    }
}