'''
  문제: 백준 1520번 - 내리막 길 (Gold 3)
  링크: https://www.acmicpc.net/problem/1520
  알고리즘: DP (Dynamic Programming), 그래프 탐색 (DFS)
  
  [분석 및 암기 패턴: DFS + Memoization]
  1. 문제 유형
     - (0,0)에서 (M-1, N-1)까지 가는 '모든 경로의 개수'를 구하는 문제.
     
  2. 왜 BFS가 아닌가?
     - BFS는 '최단 거리'를 구할 때 유리합니다. 
     - 경로의 '개수'를 셀 때는 큐에 중복된 상태가 너무 많이 쌓여 메모리와 시간이 터집니다.
     
  3. 왜 단순 DFS는 안 되나?
     - 맵 크기가 500x500일 때, 단순히 재귀만 돌리면 왔던 길을 또 탐색하게 됩니다.
     - 시간 복잡도가 지수적으로 늘어나 시간 초과(TLE)가 발생합니다.
  
  [핵심 로직: 메모이제이션 (Memoization)]
  - 정의: dp[x][y] = "(x, y) 지점에서 도착점까지 갈 수 있는 경로의 수"
  - 초기화: 모든 값을 -1로 초기화합니다.
    * -1: 아직 방문하지 않음 (계산 필요)
    * 0: 방문했는데 갈 길이 없음 (또는 도착점까지 가는 경로가 0개)
    * 양수: 도착점까지 가는 경로의 개수 (계산 완료)
'''

import sys
# [중요] 파이썬의 기본 재귀 깊이는 1,000입니다.
# 500x500 맵에서 DFS를 끝까지 돌려면 깊이가 부족하므로 반드시 늘려야 합니다.
sys.setrecursionlimit(10 ** 6)
input = sys.stdin.readline

M, N = 0, 0
board = []
dp = []
dx = [-1, 1, 0, 0]
dy = [0, 0, -1, 1]

def dfs(x, y):
    # 1. Base Case: 도착점(우측 하단)에 도달하면 경로 1개를 찾은 것임
    if x == M - 1 and y == N - 1:
        return 1
        
    # 2. Memoization: 이미 계산한 적 있는 좌표라면?
    # -1이 아니라는 것은 이미 방문해서 끝까지 가는 경로 수를 구해놨다는 뜻.
    # 다시 탐색하지 않고 저장된 값(Cache)을 바로 리턴합니다.
    if dp[x][y] != -1:
        return dp[x][y]
        
    # 3. 탐색 시작 (방문 표시)
    # 아직 계산 안 된 곳(-1)이니, 일단 0으로 만들고 탐색을 시작합니다.
    dp[x][y] = 0
    
    for i in range(4):
        nx = x + dx[i]
        ny = y + dy[i]
        
        # 맵 범위 안에 있고
        if 0 <= nx < M and 0 <= ny < N:
            # 내리막 길 조건: 현재 위치보다 높이가 낮은 곳으로만 이동 가능
            if board[nx][ny] < board[x][y]:
                # (x,y)의 경로 수 += 다음 칸(nx,ny)에서 도착점까지 가는 경로 수
                dp[x][y] += dfs(nx, ny)
                
    # 4. 상하좌우 탐색이 끝난 후, 최종 합산된 경로의 수를 리턴 (값 확정)
    return dp[x][y]

def main():
    global M, N, board, dp
    
    # 세로(M), 가로(N) 입력
    M, N = map(int, input().split())
    
    # 지도 정보 입력
    board = [list(map(int, input().split())) for _ in range(M)]
    
    # DP 테이블 초기화
    # visited 역할을 겸함 (-1: 미방문, 0 이상: 방문)
    dp = [[-1] * N for _ in range(M)]
    
    # (0, 0)에서 출발하여 도착점까지 가는 총 경로의 수 출력
    print(dfs(0, 0))

if __name__ == "__main__":
    main()