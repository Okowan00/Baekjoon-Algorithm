"""
  문제: 백준 14502번 - 연구소 (Gold 4)
  링크: https://www.acmicpc.net/problem/14502
  알고리즘: 완전탐색(Brute Force), 너비 우선 탐색(BFS)

  [시간/공간 복잡도 분석]
  1. 시간 복잡도: O( (N*M)^3 * (N*M) )
     - 벽을 세울 수 있는 빈 칸의 개수를 E라고 할 때, 벽 3개를 고르는 조합은 E_C_3 입니다.
     - N, M은 최대 8이므로 빈 칸(E)은 최대 64개입니다.
     - 조합의 수: 64_C_3 = (64*63*62) / (3*2*1) = 41,664가지 (경우의 수가 적음)
     - 각 경우마다 BFS를 돌림: BFS는 모든 칸(Vertex)을 한 번씩 방문하므로 O(N*M)
     - 총 연산량: 약 41,664 * 64 ≈ 2,666,496회
     - 결론: 파이썬의 연산 속도(보통 1초에 2~5천만 번)를 고려할 때 2초 제한 안에 충분히 통과합니다.
  
  2. 공간 복잡도: O(N*M)
     - 지도의 크기(N*M)만큼의 배열과, BFS를 위한 큐(Queue)가 필요합니다.
     - N, M <= 8 이므로 메모리는 매우 여유롭습니다.

  [풀이 핵심 전략]
  1. '빈 칸(0)'의 좌표 리스트와 '바이러스(2)'의 좌표 리스트를 따로 저장합니다.
  2. 빈 칸 리스트에서 `combinations`를 사용하여 3개의 좌표를 뽑습니다.
  3. 뽑힌 3개의 좌표에 벽을 세웠다고 가정하고(`temp_board`), BFS로 바이러스를 퍼뜨립니다.
  4. 바이러스가 다 퍼진 후 남은 안전 영역(0)의 개수를 세어 최댓값을 갱신합니다.
"""

import sys
from collections import deque
from itertools import combinations

# 빠른 입력을 위한 설정
input = sys.stdin.readline

# 전역 변수 설정
N, M = map(int, input().split())
board = [list(map(int, input().split())) for _ in range(N)]

empties = []  # 벽을 세울 수 있는 빈 칸 후보
viruses = []  # 초기 바이러스 위치

# 상하좌우 이동 방향 벡터
dx = [-1, 1, 0, 0]
dy = [0, 0, -1, 1]

def main():
    # 1. 초기 맵 스캔 (빈 칸과 바이러스 위치 저장)
    for i in range(N):
        for j in range(M):
            if board[i][j] == 0:
                empties.append((i, j))
            elif board[i][j] == 2:
                viruses.append((i, j))

    max_safe_area = 0

    # 2. 벽 3개를 세우는 모든 경우의 수 확인 (완전탐색)
    # combinations(리스트, 3) : 리스트에서 순서 상관없이 3개를 뽑음
    for walls in combinations(empties, 3):
        
        # 3. BFS 실행 후 안전 영역 크기 계산
        current_safe_area = bfs(walls)
        
        # 최댓값 갱신
        if current_safe_area > max_safe_area:
            max_safe_area = current_safe_area

    print(max_safe_area)

# 바이러스를 퍼뜨리고 안전 영역 크기를 반환하는 함수
def bfs(picked_walls):
    # 지도 복사 (원본 board를 훼손하면 안 되므로)
    # [:] 슬라이싱 복사가 deepcopy보다 빠름
    temp_board = [row[:] for row in board]

    # 선택된 위치에 벽 3개 세우기
    for r, c in picked_walls:
        temp_board[r][c] = 1

    # BFS를 위한 큐 초기화 (초기 바이러스 위치 넣기)
    q = deque(viruses)

    while q:
        x, y = q.popleft()

        for i in range(4):
            nx, ny = x + dx[i], y + dy[i]

            # 맵 범위 체크
            if 0 <= nx < N and 0 <= ny < M:
                # 빈 칸(0)이라면 바이러스가 확산됨
                if temp_board[nx][ny] == 0:
                    temp_board[nx][ny] = 2 # 바이러스로 변경
                    q.append((nx, ny))

    # 4. 안전 영역(0) 개수 카운트
    # 2차원 리스트를 순회하며 0의 개수 합산
    safe_count = 0
    for row in temp_board:
        safe_count += row.count(0)

    return safe_count

if __name__ == "__main__":
    main()