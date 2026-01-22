'''
  문제: 백준 7662번 - 이중 우선순위 큐 (Gold 4)
  링크: https://www.acmicpc.net/problem/7662
  알고리즘: 자료구조 (Min Heap + Max Heap), 맵(Map) 또는 동기화 처리
  
  [분석 및 핵심 포인트: 두 개의 힙 동기화]
  1. 문제 상황
     - 숫자를 넣는 건 똑같은데, 삭제 명령이 두 종류입니다.
     - "최댓값 삭제" 그리고 "최솟값 삭제".
     
  2. 난관
     - Min Heap을 쓰면 '최솟값'은 바로 삭제되지만, '최댓값'을 삭제하려면 O(N)이 걸립니다.
     - Max Heap을 쓰면 반대 상황이 벌어집니다.
     - 리스트를 써서 sort하면 시간 초과(TLE)가 납니다. (연산이 100만 번!)
     
  3. 해결책: "힙을 두 개 쓴다 & 게으른 삭제(Lazy Deletion)"
     - Min Heap과 Max Heap을 둘 다 만듭니다.
     - 삽입할 땐 양쪽에 다 넣습니다.
     - 삭제할 땐?
       * 여기서 핵심! 한쪽에서 삭제된 숫자가 다른 쪽 힙에는 남아있는 "유령(Ghost)" 상태가 됩니다.
       * 당장 찾아서 지우려면 느리니까, "나중에 뺄 때(Pop 할 때) 유령이면 버리자"는 전략을 씁니다.
     - 이를 위해 각 숫자에 '고유 ID'를 붙이고, 'visited' 배열로 생존 여부를 체크합니다.
'''

import sys
import heapq

input = sys.stdin.readline

def solve():
    # 연산의 개수 k
    k = int(input())
    
    # 힙 두 개 준비
    min_heap = []
    max_heap = []
    
    # 각 노드의 생존 여부를 기록할 리스트 (인덱스 = ID)
    # k가 최대 100만이니, 최대 100만 개의 ID가 생길 수 있음
    visited = [False] * k
    
    for i in range(k):
        # 입력: 'I 36' 또는 'D 1'
        command, num = input().split()
        num = int(num)
        
        if command == 'I':
            # 1. 삽입 연산
            # (숫자, ID) 형태로 넣어서 나중에 ID로 생존 여부 확인
            # i를 ID로 사용 (0부터 k-1까지 유니크함)
            
            # Min Heap엔 그냥 넣음
            heapq.heappush(min_heap, (num, i))
            
            # Max Heap엔 부호를 뒤집어서 넣음 (파이썬 팁: -num)
            heapq.heappush(max_heap, (-num, i))
            
            # 이 ID(i)는 현재 살아있음 표시
            visited[i] = True
            
        else:
            # 2. 삭제 연산
            if num == 1: # 최댓값 삭제
                # 삭제하기 전에, 이미 다른 쪽에서 지워진 '유령'들은 싹 치운다
                # "힙이 비어있지 않고 & 맨 위에 있는 놈이 이미 죽은 놈(False)이라면" -> 버림
                while max_heap and not visited[max_heap[0][1]]:
                    heapq.heappop(max_heap)
                
                # 유령 다 치우고 진짜가 남아있다면 삭제 진행
                if max_heap:
                    value, _id = heapq.heappop(max_heap)
                    visited[_id] = False # "너 죽었다" 표시 (Min Heap 쪽에도 알려주는 역할)
                    
            else: # 최솟값 삭제 (num == -1)
                # 역시 유령 먼저 청소 (Min Heap 기준)
                while min_heap and not visited[min_heap[0][1]]:
                    heapq.heappop(min_heap)
                
                # 진짜 삭제 진행
                if min_heap:
                    value, _id = heapq.heappop(min_heap)
                    visited[_id] = False # Max Heap 쪽에도 알려줌
    
    # 3. 모든 연산이 끝나고 결과 출력 전, 마지막 청소
    # (삭제되지 않고 남아있는 유령들이 탑에 있을 수 있으므로)
    while max_heap and not visited[max_heap[0][1]]:
        heapq.heappop(max_heap)
    while min_heap and not visited[min_heap[0][1]]:
        heapq.heappop(min_heap)
        
    if not min_heap or not max_heap:
        print("EMPTY")
    else:
        # Max Heap은 -값으로 저장돼 있으니 - 붙여서 복구
        print(-max_heap[0][0], min_heap[0][0])

def main():
    # 테스트 케이스 개수 T
    T = int(input())
    for _ in range(T):
        solve()

if __name__ == "__main__":
    main()