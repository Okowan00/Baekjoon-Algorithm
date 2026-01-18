/*
  문제: 백준 9935번 - 문자열 폭발 (Gold 4)
  링크: https://www.acmicpc.net/problem/9935
  알고리즘: 자료구조 (Stack), 문자열
  
  [암기할 패턴: 문자열 폭발 처리]
  1. 자바에서 String은 불변(Immutable)이라서 '+' 연산이나 replace를 반복하면 메모리/시간 터짐.
  2. 가변 문자열인 `StringBuilder`를 스택(Stack)처럼 사용해야 함.
  3. 한 글자씩 쌓다가(push), 폭발 문자열 길이만큼 쌓이면 뒤에서부터 검사.
  4. 일치하면? `sb.setLength()`로 뒤를 잘라버림 (pop 효과).
*/

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        // 1. 입력 속도를 위해 BufferedReader 필수
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        String str = br.readLine();      // 전체 문자열
        String bomb = br.readLine();     // 폭발 문자열 (예: C4)
        int bombLen = bomb.length();     // 폭발 문자열 길이 미리 저장
        
        // 스택처럼 쓸 StringBuilder (정답을 담을 곳)
        StringBuilder sb = new StringBuilder();

        // 2. 전체 문자열을 한 글자씩 탐색
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            sb.append(ch); // 일단 스택에 넣음 (Push)

            // 3. 스택에 쌓인 길이가 폭발 문자열보다 길어지면 검사 시작
            if (sb.length() >= bombLen) {
                boolean isSame = true;

                // 뒤에서부터 폭발 문자열과 일치하는지 비교
                for (int j = 0; j < bombLen; j++) {
                    // sb의 뒤쪽 글자와 bomb의 글자 비교
                    // sb.length() - bombLen + j : sb의 비교 시작 위치
                    if (sb.charAt(sb.length() - bombLen + j) != bomb.charAt(j)) {
                        isSame = false;
                        break; // 하나라도 다르면 탈출
                    }
                }

                // 4. 폭발 문자열과 똑같다면? -> 스택에서 제거 (Pop)
                if (isSame) {
                    // 길이를 줄여버리면 뒤에 있는 문자들은 자동으로 날아감 (아주 중요!)
                    sb.setLength(sb.length() - bombLen);
                }
            }
        }

        // 5. 결과 출력 (비어있으면 FRULA)
        if (sb.length() == 0) {
            System.out.println("FRULA");
        } else {
            System.out.println(sb.toString());
        }
    }
}