package fun.vyse.cloud.data.shield.process;

public interface IDataProcess {
    String encrypt(String content) throws Exception;

    String decode(String content) throws Exception;
}
