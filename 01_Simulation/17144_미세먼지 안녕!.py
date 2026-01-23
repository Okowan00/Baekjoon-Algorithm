'''
  문제: 백준 17144번 - 미세먼지 안녕! (Gold 4)
  링크: https://www.acmicpc.net/problem/17144
  알고리즘: 구현 (Simulation), 배열 회전 (Array Rotation)
  
  [분석 및 핵심 포인트]
  1. 문제 상황
     - R x C 격자판에서 미세먼지가 확산되고, 공기청정기가 작동합니다.
     - 이 과정을 T초 동안 반복한 뒤, 남아있는 미세먼지의 양을 구해야 합니다.

  2. 함정 및 주의사항 (Simultaneous Update)
     - [확산 단계]: "모든 칸에서 동시에" 일어납니다.
     - (1,1)의 먼지가 (1,2)로 확산되었다고 해서, 그 즉시 (1,2)의 먼지 양이 늘어나면 안 됩니다.
     - (1,2)가 확산될 때는 "원래 가지고 있던 양"을 기준으로 계산해야 합니다.
     - 해결책: 확산된 양을 담을 '임시 배열'을 쓰거나, 변화량만 기록했다가 한 번에 합쳐야 합니다.

  3. 배열 회전 (노가다 vs 테크닉)
     - 공기청정기 바람에 따라 먼지가 이동합니다.
     - 위쪽 청정기: 반시계 방향 / 아래쪽 청정기: 시계 방향.
     - 인덱스를 하나씩 밀어주는(Shift) 작업을 실수 없이 구현하는 것이 핵심입니다.
'''

import sys
input = sys.stdin.readline

# 1. 입력 받기 (R:행, C:열, T:시간)
R, C, T = map(int, input().split())
board = [list(map(int, input().split())) for _ in range(R)]

# 2. 공기청정기 위치 찾기 (항상 1열에 있음)
# 위쪽 청정기의 행 좌표를 up, 아래쪽을 down으로 저장
up = -1
down = -1

for r in range(R):
    if board[r][0] == -1:
        up = r
        down = r + 1
        break

# 3. 미세먼지 확산 함수
def spread():
    # 변화량을 담을 임시 배열 (모든 칸이 0인 상태)
    # diff[r][c] = (r,c)에 "들어오거나 나간" 미세먼지의 총량
    diff = [[0] * C for _ in range(R)]

    dx = [0, 0, 1, -1]
    dy = [1, -1, 0, 0]

    for r in range(R):
        for c in range(C):
            # 미세먼지가 있고, 공기청정기가 아니라면 확산 시도
            if board[r][c] > 0:
                amount = board[r][c] // 5 # 확산되는 양
                if amount == 0: continue  # 0이면 확산 안 함

                count = 0 # 확산된 방향 개수
                for i in range(4):
                    nr = r + dx[i]
                    nc = c + dy[i]

                    # 범위 안이고, 공기청정기(-1)가 아니면 확산
                    if 0 <= nr < R and 0 <= nc < C and board[nr][nc] != -1:
                        diff[nr][nc] += amount # 옆 칸에 먼지 추가
                        count += 1
                
                # 내 칸에서 빠져나간 먼지 계산 (확산량 * 방향 개수)
                board[r][c] -= (amount * count)

    # 확산 계산이 다 끝나면, 변화량을 원본 맵에 한 번에 적용 (동시성 처리)
    for r in range(R):
        for c in range(C):
            board[r][c] += diff[r][c]

# 4. 공기청정기 작동 (바람 이동)
def air_clean():
    # [위쪽 청정기: 반시계 방향]
    # 바람의 흐름: 청정기 -> 오른쪽 -> 위 -> 왼쪽 -> 아래 -> 청정기
    # 데이터를 덮어쓰지 않으려면 "바람의 역방향"으로 당겨오는 게 편함
    
    # 1) 왼쪽 열 (위에서 아래로 당김) ↓
    for r in range(up - 1, 0, -1):
        board[r][0] = board[r-1][0]
    
    # 2) 위쪽 행 (오른쪽에서 왼쪽으로 당김) ←
    for c in range(C - 1):
        board[0][c] = board[0][c+1]
        
    # 3) 오른쪽 열 (아래에서 위로 당김) ↑
    for r in range(up):
        board[r][C-1] = board[r+1][C-1]
        
    # 4) 아래쪽 행 (청정기 쪽에서 오른쪽으로 밀려남) →
    # (주의: 청정기 바로 오른쪽은 0이 됨)
    for c in range(C - 1, 1, -1):
        board[up][c] = board[up][c-1]
    
    # 청정기에서 바로 나온 바람은 깨끗함 (먼지 0)
    board[up][1] = 0


    # [아래쪽 청정기: 시계 방향]
    # 바람의 흐름: 청정기 -> 오른쪽 -> 아래 -> 왼쪽 -> 위 -> 청정기
    
    # 1) 왼쪽 열 (아래에서 위로 당김) ↑
    for r in range(down + 1, R - 1):
        board[r][0] = board[r+1][0]
        
    # 2) 아래쪽 행 (오른쪽에서 왼쪽으로 당김) ←
    for c in range(C - 1):
        board[R-1][c] = board[R-1][c+1]
        
    # 3) 오른쪽 열 (위에서 아래로 당김) ↓
    for r in range(R - 1, down, -1):
        board[r][C-1] = board[r-1][C-1]
        
    # 4) 위쪽 행 (청정기 쪽에서 오른쪽으로 밀려남) →
    for c in range(C - 1, 1, -1):
        board[down][c] = board[down][c-1]
        
    # 청정기에서 바로 나온 바람은 0
    board[down][1] = 0

def main():
    # T초 동안 시뮬레이션 반복
    for _ in range(T):
        spread()     # 1. 확산
        air_clean()  # 2. 공기청정

    # 최종 미세먼지 양 계산
    total_dust = 0
    for r in range(R):
        for c in range(C):
            if board[r][c] > 0:
                total_dust += board[r][c]
                
    print(total_dust)

if __name__ == "__main__":
    main()