package hello.jdbc.repository;

import hello.jdbc.connection.ConnectionConst;
import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

// JDBC - DriverManager 사용
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member)throws SQLException{
        String sql = "insert into member(member_id, money) values (?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            //값, 타입을 적어줘야함.
            pstmt.setString(1,member.getMemberId());
            pstmt.setInt(2,member.getMoney());
            pstmt.executeUpdate();//query 실행 반환값은 실제 DB에 영향을 미친 row수
            return member;

        } catch (SQLException e) {
            log.error("db error",e);
            throw e;
        }finally {
            close(con,pstmt,null);
        }
    }
    public Member findById(String memberId) throws SQLException{
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);

            //업데이트가 아니므로 excuteQuery()
            rs = pstmt.executeQuery();
            //rs 한 번은 호출해줘야함. 처음에는 아무것도 가르키지 않음.
            if (rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else{
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }
        }catch(SQLException e){
            log.error("db error",e);
            throw e;
        }finally {
            close(con,pstmt,rs);
        }
    }

    public void update(String memberId, int money) throws SQLException{
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            //값, 타입을 적어줘야함.
            pstmt.setInt(1,money);
            pstmt.setString(2,memberId);
            int resultSize = pstmt.executeUpdate();//query 실행 반환값은 실제 DB에 영향을 미친 row수
            log.info("resultSize {}",resultSize);

        } catch (SQLException e) {
            log.error("db error",e);
            throw e;
        }finally {
            close(con,pstmt,null);
        }

    }

    public void delete(String memberId) throws SQLException{
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            //값, 타입을 적어줘야함.
            pstmt.setString(1,memberId);
            pstmt.executeUpdate();//query 실행 반환값은 실제 DB에 영향을 미친 row수

        } catch (SQLException e) {
            log.error("db error",e);
            throw e;
        }finally {
            close(con,pstmt,null);
        }

    }

    //Statement는 바인딩이 안되지만
    //PrepareStatement는 바인딩이 됨.
    private void close(Connection con, Statement stmt, ResultSet rs){

        if(rs != null){
            try {
                rs.close(); /* SQLException */
            } catch (SQLException e) {
                log.info("error",e);
            }
        }

        if(stmt != null){
            try {
                stmt.close(); /* SQLException */
            } catch (SQLException e) {
                log.info("error",e);
            }
        }

        if(con != null){
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error",e);
            }
        }

    }


    private Connection getConnection(){
        return DBConnectionUtil.getConnection();
    }


}
