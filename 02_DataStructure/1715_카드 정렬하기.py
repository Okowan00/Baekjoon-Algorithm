'''
  문제: 백준 1715번 - 카드 정렬하기 (Gold 4)
  링크: https://www.acmicpc.net/problem/1715
  알고리즘: 자료구조 (Priority Queue/Min Heap), 그리디 (Greedy)
  
  [분석 및 암기 패턴: 최소 힙 (Min Heap)]
  1. 문제 핵심
     - 카드 묶음이 여러 개 있을 때, 두 개씩 합쳐서 하나로 만들어야 함.
     - 두 묶음(A, B)을 합칠 때 발생하는 비용은 A + B.
     - "총 비교 횟수(비용)를 최소"로 만드는 것이 목표.
     
  2. 왜 자료구조(Heap)를 써야 하는가?
     - 비용을 최소화하려면 무조건 **"현재 가장 작은 두 묶음"**을 골라서 합쳐야 함.
     - 단순히 처음에 정렬(Sort)만 해두면 안 됨. 
       -> 합쳐진 새로운 묶음이 다시 리스트에 들어갔을 때, 걔가 또 가장 작은 녀석일 수도 있고 아닐 수도 있기 때문.
     - 즉, **"넣고 빼는 과정에서 항상 최소값이 자동으로 정렬되어 나오는"** 자료구조인 **최소 힙(Min Heap)**이 필요함.
     
  3. 시간 복잡도
     - N이 최대 100,000.
     - 일반 리스트에서 최소값 찾기/정렬은 O(N) 또는 O(NlogN). 이걸 N-1번 반복하면 O(N^2)로 시간 초과.
     - 힙(Heap)을 쓰면 삽입/삭제가 O(logN). 총 O(NlogN)으로 통과 가능.
'''

import sys
import heapq # 파이썬의 우선순위 큐(Heap) 모듈
input = sys.stdin.readline

def main():
    # N: 카드 묶음의 개수
    N = int(input())
    
    # 카드 묶음들을 담을 힙(Heap) 리스트
    cards = []
    
    # 입력받으면서 바로 힙에 넣기
    for _ in range(N):
        # heappush: 값을 넣으면 알아서 정렬(최소 힙 유지)해줌 -> O(logN)
        heapq.heappush(cards, int(input()))
        
    # 예외 처리: 카드 묶음이 1개라면 비교할 필요가 없음 (비용 0)
    if N == 1:
        print(0)
        return

    total_cost = 0
    
    # 묶음이 딱 하나 남을 때까지 반복 (N-1번 반복)
    while len(cards) > 1:
        # 1. 가장 작은 묶음 두 개 꺼내기 (Pop) -> O(logN)
        first = heapq.heappop(cards)
        second = heapq.heappop(cards)
        
        # 2. 두 묶음을 합치기 (비용 발생)
        current_sum = first + second
        total_cost += current_sum
        
        # 3. 합친 묶음을 다시 힙에 넣기 (Push) -> O(logN)
        # 이제 이 current_sum도 다른 묶음들과 경쟁해서 최소값을 따져야 함
        heapq.heappush(cards, current_sum)
        
    print(total_cost)

if __name__ == "__main__":
    main()