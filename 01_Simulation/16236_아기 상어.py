"""
  문제: 백준 16236번 - 아기 상어 (Gold 3)
  링크: https://www.acmicpc.net/problem/16236
  알고리즘: 너비 우선 탐색(BFS), 시뮬레이션(Simulation), 정렬(Sorting)

  [시간/공간 복잡도 분석]
  1. 시간 복잡도: O(N^4)
     - 맵의 크기(N)가 최대 20으로 매우 작습니다.
     - 상어가 물고기를 한 마리 먹을 때마다 BFS를 수행하여 최단 거리를 찾습니다.
     - 최악의 경우, 맵의 모든 칸(N^2)에 물고기가 있고 하나씩 다 먹어야 한다고 가정해봅시다.
     - BFS 한 번의 시간 복잡도: O(V+E) = O(N^2) (모든 칸 탐색)
     - 최대 물고기 마리 수: N^2
     - 총 연산량: O(N^2 * N^2) = O(N^4)
     - N=20일 때, 160,000번 정도의 연산이므로 2초(약 2억 번 연산 가능) 시간 제한 안에 아주 넉넉하게 통과합니다.
  
  2. 공간 복잡도: O(N^2)
     - 지도(N*N)와 방문 체크 배열(visited), 큐(Queue)에 필요한 공간입니다.
     - N <= 20 이므로 메모리 사용량은 미미합니다.

  [풀이 핵심 전략]
  1. BFS의 역할: 단순히 '도착'하는 것이 목표가 아니라, '먹을 수 있는 물고기 후보'를 모두 찾는 것이 목표입니다.
  2. 우선순위 처리: 문제 조건(거리 짧은 순 -> 위쪽 -> 왼쪽)을 만족하기 위해,
     먹을 수 있는 물고기를 찾으면 바로 먹지 않고 `candidates` 리스트에 담습니다.
  3. 정렬(Sorting): BFS가 끝난 후 `candidates` 리스트를 (거리, 행, 열) 순서로 오름차순 정렬하여 가장 적합한 물고기를 선택합니다.
  4. 시뮬레이션 루프:
     - 물고기를 먹으면 그 자리로 상어를 이동시키고, 시간을 더하고, 지도를 빈칸(0)으로 갱신합니다.
     - 상어의 크기만큼 물고기를 먹으면 크기(size)를 +1 하고 카운트를 초기화합니다.
     - 더 이상 먹을 물고기가 없을 때까지 이 과정을 무한 반복합니다.
"""

import sys
from collections import deque

# 빠른 입력을 위한 설정
input = sys.stdin.readline

# 전역 변수 설정
N = int(input())
board = [list(map(int, input().split())) for _ in range(N)]

# 상하좌우 이동 방향 벡터
dx = [-1, 1, 0, 0]
dy = [0, 0, -1, 1]

# 상어의 상태 변수
shark_x, shark_y = 0, 0
shark_size = 2
eat_count = 0

def main():
    global shark_x, shark_y, shark_size, eat_count

    # 1. 초기 상어 위치 찾기
    # 지도에서 상어 위치를 찾은 뒤, 그 자리를 0(빈 칸)으로 만듭니다.
    # (상어는 변수로 따로 관리하므로 지도에 남겨둘 필요가 없습니다)
    for i in range(N):
        for j in range(N):
            if board[i][j] == 9:
                shark_x, shark_y = i, j
                board[i][j] = 0

    total_time = 0

    # 2. 시뮬레이션 시작 (먹을 물고기가 없을 때까지 반복)
    while True:
        # BFS로 먹을 수 있는 가장 가까운 물고기 찾기
        target = bfs(shark_x, shark_y, shark_size)

        # 먹을 수 있는 물고기가 없으면 엄마 상어에게 도움 요청 (종료)
        if target is None:
            break

        # 3. 물고기 먹기 및 상태 갱신
        dist, nx, ny = target
        total_time += dist        # 이동 시간 추가
        shark_x, shark_y = nx, ny # 상어 위치 이동
        board[nx][ny] = 0         # 물고기 냠냠 (빈 칸으로 변경)
        eat_count += 1            # 먹은 개수 증가

        # 4. 상어 성장 확인
        # 자신의 크기만큼 먹었으면 크기 1 증가
        if eat_count == shark_size:
            shark_size += 1
            eat_count = 0
            
    print(total_time)

# 현재 위치에서 먹을 수 있는 물고기를 찾아 반환하는 함수
def bfs(sx, sy, size):
    # 거리 저장 및 방문 체크 (매번 새로 생성해야 함)
    visited = [[-1] * N for _ in range(N)]
    q = deque([(sx, sy)])
    visited[sx][sy] = 0 # 시작점 거리는 0

    candidates = [] # 먹을 수 있는 물고기 후보들

    while q:
        x, y = q.popleft()

        for i in range(4):
            nx, ny = x + dx[i], y + dy[i]

            # 맵 범위 체크 및 방문 여부 확인
            if 0 <= nx < N and 0 <= ny < N and visited[nx][ny] == -1:
                # [이동 조건] 자신의 크기보다 작거나 같은 곳만 지나갈 수 있음
                if board[nx][ny] <= size:
                    visited[nx][ny] = visited[x][y] + 1
                    q.append((nx, ny))

                    # [먹기 조건] 자신의 크기보다 작은 물고기만 먹을 수 있음 (0은 빈칸이므로 제외)
                    if 0 < board[nx][ny] < size:
                        candidates.append((visited[nx][ny], nx, ny))

    # BFS 탐색 종료 후 후보 정렬
    if candidates:
        # 우선순위: 1.거리(오름차순) -> 2.행(위쪽) -> 3.열(왼쪽)
        # 파이썬의 sort는 튜플의 앞 원소부터 순서대로 비교하므로 조건 자동 만족
        candidates.sort(key=lambda x: (x[0], x[1], x[2]))
        return candidates[0] # 가장 우선순위 높은 물고기 반환
    else:
        return None

if __name__ == "__main__":
    main()