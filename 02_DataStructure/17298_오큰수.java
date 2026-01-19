/*
  문제: 백준 17298번 - 오큰수 (Gold 4)
  링크: https://www.acmicpc.net/problem/17298
  알고리즘: 자료구조 (스택 Stack)
  
  [암기 패턴: 스택을 이용한 O(N) 풀이]
  1. 스택에는 '값'이 아니라 '인덱스(Index)'를 넣습니다.
     - 스택에 들어있다는 건 "아직 나보다 큰 수(오큰수)를 못 만난 숫자들"이라는 뜻입니다.
  
  2. 로직 (for문 한 번만 순회):
     - 현재 숫자(A[i])가 스택의 맨 위(top)가 가리키는 숫자보다 크다면?
     - "어! 내가 너의 오큰수다!" -> 스택에서 pop 하고 정답 배열에 기록.
     - 작거나 스택이 비었으면? -> "아직 짝 못 찾음" -> 스택에 내 인덱스 push.
  
  3. 마무리:
     - 끝까지 돌았는데 스택에 남아있는 인덱스들은?
     - 오큰수가 없는 애들이므로 전부 -1 처리.
*/

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        int N = Integer.parseInt(br.readLine());
        int[] seq = new int[N]; // 수열 배열
        int[] ans = new int[N]; // 정답 배열 (오큰수 저장)
        
        StringTokenizer st = new StringTokenizer(br.readLine());
        for (int i = 0; i < N; i++) {
            seq[i] = Integer.parseInt(st.nextToken());
        }

        // 스택 생성 (아직 오큰수를 못 찾은 '인덱스'들을 모아두는 대기실)
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < N; i++) {
            // [핵심 로직]
            // 스택이 비어있지 않고, 
            // 현재 보고 있는 숫자(seq[i])가 스택 맨 위(seq[stack.peek()])보다 크다면?
            // -> 오큰수 발견!
            while (!stack.isEmpty() && seq[stack.peek()] < seq[i]) {
                int index = stack.pop(); // 짝을 찾은 녀석의 인덱스를 꺼냄
                ans[index] = seq[i];     // 그 녀석의 오큰수는 바로 '나(seq[i])'다!
            }
            
            // 현재 인덱스(i)를 대기실(스택)에 넣음 (아직 나의 오큰수는 안 나왔으니까)
            stack.push(i);
        }

        // for문이 다 끝났는데 스택에 남아있는 인덱스들
        // = 끝까지 가도 나보다 큰 놈을 못 만난 슬픈 녀석들
        while (!stack.isEmpty()) {
            ans[stack.pop()] = -1;
        }

        // [출력 최적화] StringBuilder 필수 (N이 100만이라 System.out.print 쓰면 느림)
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < N; i++) {
            sb.append(ans[i]).append(" ");
        }
        System.out.println(sb);
    }
}