package applications;

public class KbasedMedianApplication {
    public static void main(String[] args){
        for(int i = 0; i<10; i++){
            for(int j = 1; j<=8; j++){
                String[] val = {String.valueOf(j)};
                empirical_FP_KNN.main(val);
                model_FP_KNN.main(val);
            }
        }
    }
}
