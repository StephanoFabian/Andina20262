package pe.edu.upc.demosm2.dtos;
import java.util.List;
public class PaginaDTO<T> {
 private List<T> content;
 private long totalElements;
 private int number;
 private int size;
 public PaginaDTO() {}
 public PaginaDTO(List<T> content,long totalElements,int number,int size) {this.content=content;this.totalElements=totalElements;this.number=number;this.size=size;}
 public List<T> getContent(){return content;}
 public void setContent(List<T> value){content=value;}
 public long getTotalElements(){return totalElements;}
 public void setTotalElements(long value){totalElements=value;}
 public int getNumber(){return number;}
 public void setNumber(int value){number=value;}
 public int getSize(){return size;}
 public void setSize(int value){size=value;}
}
