# arcus_test
arcus_test


본 프로그램은 아커스와 DB의 속도를 비교하기 위해 만들어진 프로그램입니다.

1~100000사이의 랜덤한 값을 가진 10000개의 Element를 삽입한 후에 1000~2000사이의 일괄 조회를 하는데 걸리는 속도를 계산하여 출력합니다.

HelloArcus.java에서는 로컬 아이피로 Arcus서버에 접속하여 결과를 냅니다. B+Tree를 쓰고 있습니다. ip를 바꾸면 외부 접속도 가능합니다.

DBList.java에서는 root:1234 로컬DB에 접속하여 test 데이터베이스에서 쿼리문을 처리합니다. mysql과 jdbc가 설치되어있어야 합니다.

mvn로 빌드 되었고 이클립스로도 import하여 테스트 해볼 수 있습니다.
