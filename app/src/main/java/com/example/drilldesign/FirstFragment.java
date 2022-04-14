package com.example.drilldesign;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.drilldesign.databinding.FragmentFirstBinding;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FirstFragment extends Fragment {




    private FragmentFirstBinding binding;
    MainActivity mainActivity = new MainActivity();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle resultCalculation = new Bundle();
                resultCalculation.putString("resultCalculation", doCalculate());
                resultCalculation.putString("initialData", getInitialData());


                //doCalculate();

                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment, resultCalculation);

            }
        });
    }


    public String doCalculate() {


        //-----------------
        //START // Получение всех переменных
        //-----------------
        double holeDiameter;
        if (binding.holeDiameter.getText().toString().length() == 0) {
            holeDiameter = 20.0;
        } else {
            holeDiameter = Double.parseDouble(binding.holeDiameter.getText().toString());
        }


        boolean isFinish = binding.finishingCheckbox.isChecked();
        int workpieceMaterial = binding.workpieceSpinner.getSelectedItemPosition();
        int machine = binding.machineSpinner.getSelectedItemPosition();
        double tolerance;
        if (binding.editTolerance.getText().toString().length() == 0)
            tolerance = 0.13;
        else tolerance = Double.parseDouble(binding.editTolerance.getText().toString());

        double toleranceES;
        if (binding.editToleranceEs.getText().toString().length() == 0)
            toleranceES = 0.13;
        else toleranceES = Double.parseDouble(binding.editToleranceEs.getText().toString());

        double toleranceEI;
        if (binding.editToleranceEi.getText().toString().length() == 0)
            toleranceEI = 0.0;
        else toleranceEI = Double.parseDouble(binding.editToleranceEi.getText().toString());

        //-----------------
        //END // Получение всех переменных
        //-----------------


        //Проверка условия. 14<=holeDiameter<=49

        if ((holeDiameter <= 14.0) | (holeDiameter >= 49.0)){
            System.out.println("Hole diameter is BAD " + holeDiameter);

            return getString(R.string.diameterError);

        }
        String resultCalculation = "";

        //----------------
        //1. Положение сварного шва
        //----------------
        if (holeDiameter >= 14 && holeDiameter <= 20)
            resultCalculation = resultCalculation
                    + getString(R.string.r1_weldpos) +
                    + ThreadLocalRandom.current().nextInt(22, 30)
                    + getString(R.string.millimeters) + ".\r\n";
        if (holeDiameter > 20 && holeDiameter <= 32)
            resultCalculation = resultCalculation
                    + getString(R.string.r1_weldpos)
                    + "32"
                    + getString(R.string.millimeters) + ".\r\n";
        if (holeDiameter > 32 && holeDiameter <= 40)
            resultCalculation = resultCalculation
                    + getString(R.string.r1_weldpos)
                    + "36"
                    + getString(R.string.millimeters) + ".\r\n";
        if (holeDiameter > 40 && holeDiameter <= 52)
            resultCalculation = resultCalculation
                    + getString(R.string.r1_weldpos)
                    + "40"
                    + getString(R.string.millimeters) + ".\r\n";

        //-----------------
        //2. Выбор геометрических параметров сверла
        //-----------------

        Resources resources = getResources();
        ArrayList<String> r2_2fArray = new ArrayList<String>(Arrays.asList(resources.getStringArray(R.array.r2_2f)));
        String r2_2f = r2_2fArray.get(workpieceMaterial);

        ArrayList<String> r2_wtArray = new ArrayList<String>(Arrays.asList(resources.getStringArray(R.array.r2_wt)));
        String r2_wt = r2_wtArray.get(workpieceMaterial);

        ArrayList<String> r2_atArray = new ArrayList<String>(Arrays.asList(resources.getStringArray(R.array.r2_at)));
        String r2_at = r2_atArray.get(workpieceMaterial);

        resultCalculation = resultCalculation +
                getString(R.string.r2_summary)
                + "2ф=" + r2_2f + "°; "
                + "wt=" + r2_wt + "°; "
                + "at=" + r2_at + "°.\r\n";

        //-----------------
        //3. Расчетный диаметр сверла
        //-----------------
        float r3_d = 0;
        if (isFinish){
            double dp = holeDiameter + toleranceES - 0.0737 * tolerance;
            //запас на разбивку
            double r3_razbivka1 = dp - holeDiameter + toleranceES - tolerance;
            double r3_razbivka2 = 0.0076 * Math.pow(holeDiameter, ( (double) 1/3));
            double r3_iznos1 = dp - holeDiameter + toleranceEI;
            double r3_iznos2 = 0.004 * Math.pow(holeDiameter, ( (double) 1/3));
            double round_dp = 0.0;
            if (dp <= 14)  round_dp = 0.05;
            else if (dp <= 32) round_dp = 0.1;
            else if (dp <= 50) round_dp = 0.25;



            r3_d = (float) (round_dp * (Math.round(dp / round_dp)));






            //Формирование отчета
            resultCalculation = resultCalculation + getString(R.string.r3_finish) +
                    getTrunc(dp, 100) + getString(R.string.millimeters) + "\r\n";
            if (r3_razbivka1 >= r3_razbivka2){
                resultCalculation = resultCalculation + getString(R.string.r3_razbivka) + getTrunc(r3_razbivka1, 10000) + " >= " + getTrunc(r3_razbivka2, 10000) + " " + getString(R.string.true_text) + ".\r\n";
            }
            else resultCalculation = "Error r3_razbivka";
            if (r3_iznos1 >= r3_iznos2) {
                resultCalculation = resultCalculation + getString(R.string.r3_drillWear) + getTrunc(r3_iznos1, 10000) + " >= " + getTrunc(r3_iznos2, 10000) + " " + getString(R.string.true_text) + ".\r\n";
            }
            else resultCalculation = "Error r3_iznos";

            resultCalculation = resultCalculation + getString(R.string.r3_roundDp) + round_dp + ": \r\n        d = " + r3_d + getString(R.string.millimeters) + "\r\n";
        }
        else {
            resultCalculation = resultCalculation + getString(R.string.r3_isNotFinish) + holeDiameter + getString(R.string.millimeters) + "\r\n";
        }

        //---------------
        //4. Задний угол и угол наклона винтовой канавки
        //---------------


        int r4_a = (int) Math.floor((Double.parseDouble(r2_at) * ((3.33 / (r3_d - 3.25)) + 0.79)));
        int r4_w = (int) Math.floor(Double.parseDouble(r2_wt) * (1.1 - (1.624 / (r3_d + 3.5))));

        resultCalculation = resultCalculation + getString(R.string.r4_angle_a_w)
                + "\r\n        a = " + r4_a + "°\r\n        w = " + r4_w + "°\r\n";

        //--------------
        //5. Ленточка при обработке
        //--------------
        float r5_result;

        if (workpieceMaterial >= 10) {

            r5_result = (float) (Math.round((1.2 + (0.2682 * Math.log(r3_d - 18 + Math.sqrt((r3_d - 18)*(r3_d - 18) + 1))))/0.001) * 0.001);

        }
        else {
            r5_result = (float) (Math.round((0.5 * (Math.pow(r3_d, ( (double) 1/3))))/0.001) * 0.001);

        }

        resultCalculation = resultCalculation + getString(R.string.r5_ribbon) + "''" + binding.workpieceSpinner.getSelectedItem().toString()
                + "''\r\n        f = " + r5_result + getString(R.string.millimeters) + "\r\n";

        //--------------
        //6. Высота ленточки
        //--------------

        resultCalculation = resultCalculation + getString(R.string.r6_ribbon_h) + (Math.round((r3_d * 0.025)/0.001)*0.001) + getString(R.string.millimeters) + "\r\n";


        //--------------
        //7. Диаметр спинки
        //--------------

        float r7_q = (float) (Math.round((r3_d * 0.95)/0.001)*0.001);

        resultCalculation = resultCalculation + getString(R.string.r7_back) + r7_q + getString(R.string.millimeters) + "\r\n";

        //-------------
        //8. Центральный угол канавки
        //-------------

        int r8_v = 0;

        if (workpieceMaterial >= 10) {
            r8_v = 116;
        }
        else {
            r8_v = ThreadLocalRandom.current().nextInt(90,93);
        }

        resultCalculation = resultCalculation + getString(R.string.r8_groove) + "''" + binding.workpieceSpinner.getSelectedItem().toString()
                + "''\r\n        v = " + r8_v + "°\r\n";


        //--------------
        //9. Ширина пера
        //--------------

        float r9_B = (float) (Math.round((Math.abs(r3_d * Math.sin( Math.toRadians((Math.PI - r8_v) / 2)) * Math.cos( Math.toRadians(r4_w))))/0.001)*0.001);

        resultCalculation = resultCalculation + getString(R.string.r9_penW) + r9_B + getString(R.string.millimeters) + "\r\n";

        //-------------
        //10. Толщина сердцевины канавки
        //-------------
        float r10_k = 0;



        if (workpieceMaterial >= 10) {
            r10_k = (float)  ((float)((int) (ThreadLocalRandom.current().nextDouble(0.13, 0.15) * 100)) / 100) * r3_d;
        }
        else {
            r10_k = (float) (r3_d * 0.5);
        }
        float r10_k2 = (float) (r10_k + 1.4);

        resultCalculation = resultCalculation + getString(R.string.r10_result, (r10_k + ""), (r10_k2 + ""), binding.workpieceSpinner.getSelectedItem().toString()) + "\r\n";

        //--------------
        //11. Длинна сверла в общем случае
        //--------------

        resultCalculation = resultCalculation + getString(R.string.r11_output) + "\r\n";

        //11.1 Длинна отверстия
        float r11_1 = r3_d * 2;
        resultCalculation = resultCalculation + getString(R.string.r11_1_lengthDrill, (r11_1 + "")) + "\r\n";

        //11.2 Запас выхода стружки из отверстия
        float r11_2 = (float) ((double) ((int) (ThreadLocalRandom.current().nextDouble(0.3, 1) * 10)) / 10) * r3_d;
        resultCalculation = resultCalculation + getString(R.string.r11_2_, (r11_2 + "")) + "\r\n";

        //11.3 Длина кондукторной втулки
        int r11_3 = 0;
        if ((r3_d*1.5) < 28) r11_3 = 25;
        else if ((r3_d*1.5) < 30) r11_3 = 28;
        else if ((r3_d*1.5) < 32) r11_3 = 30;
        else if ((r3_d*1.5) < 35) r11_3 = 32;
        else if ((r3_d*1.5) < 36) r11_3 = 35;
        else if ((r3_d*1.5) < 40) r11_3 = 36;
        else if ((r3_d*1.5) < 45) r11_3 = 40;
        else if ((r3_d*1.5) < 50) r11_3 = 45;
        else if ((r3_d*1.5) < 56) r11_3 = 50;
        else if ((r3_d*1.5) < 63) r11_3 = 56;
        else if ((r3_d*1.5) < 67) r11_3 = 63;
        else if ((r3_d*1.5) < 78) r11_3 = 67;
        else r11_3 = 67;
        resultCalculation = resultCalculation + getString(R.string.r11_3_result, (r3_d + ""), (getTrunc((r3_d*1.5), 1000) + ""), (r11_3 + "")) + "\r\n";

        //11.4 Длина стачивания
        double r11_4 = 2.5; //нет нужной таблицы. замена на значение из примера
        resultCalculation = resultCalculation + getString(R.string.r11_4_result, (r11_4 + "")) + "\r\n";

        //11.5 Длина стружечной канавки неполной глубины
        double r11_5 = r3_d * 0.5;
        resultCalculation = resultCalculation + getString(R.string.r11_5_result, getTrunc(r11_5, 100) + "") + "\r\n";

        //11.6 Длина шейки
        double r11_6 = ThreadLocalRandom.current().nextDouble(8,12);
        resultCalculation = resultCalculation + getString(R.string.r11_6, (getTrunc(r11_6, 1000) + "")) + "\r\n";

        //11.7 Длина хвостовика и конус
        double r11_7_calLenTale = 2.5 * (r3_d + 1);
        int r11_7_tableLength;
        int r11_7_MorseCone;
        if (r11_7_calLenTale < 40) r11_7_tableLength = 40;
        else if (r11_7_calLenTale < 45) r11_7_tableLength = 45;
        else if (r11_7_calLenTale < 50) r11_7_tableLength = 50;
        else if (r11_7_calLenTale < 56) r11_7_tableLength = 56;
        else if (r11_7_calLenTale < 63) r11_7_tableLength = 63;
        else if (r11_7_calLenTale < 71) r11_7_tableLength = 71;
        else if (r11_7_calLenTale < 80) r11_7_tableLength = 80;
        else if (r11_7_calLenTale < 90) r11_7_tableLength = 90;
        else if (r11_7_calLenTale < 100) r11_7_tableLength = 100;
        else if (r11_7_calLenTale < 110) r11_7_tableLength = 110;
        else if (r11_7_calLenTale < 125) r11_7_tableLength = 125;
        else r11_7_tableLength = 140;
        if (r3_d < 18) r11_7_MorseCone = 2;
        else if (r3_d < 24.1) r11_7_MorseCone = 3;
        else if (r3_d < 31.6) r11_7_MorseCone = 4;
        else if (r3_d < 44.7) r11_7_MorseCone = 5;
        else r11_7_MorseCone = 6;
        resultCalculation = resultCalculation + getString(R.string.r11_7_result, (r11_7_MorseCone + ""), (getTrunc(r11_7_calLenTale, 1000) + ""), (r11_7_tableLength + "")) + "\r\n";

        //11.8 Длина сверла
        double r11_8_L = r11_1 + r11_2 + r11_3 + r11_4 + r11_5 + r11_6 + r11_7_tableLength;
        resultCalculation = resultCalculation + getString(R.string.r11_8_summaryLength, (getTrunc(r11_8_L,1000) + "")) + "\r\n";

        //-------------
        //12. Проверка сверла на прочность
        //-------------

        //1
        ArrayList<String> r12_machineFullNameArray = new ArrayList<String>(Arrays.asList(resources.getStringArray(R.array.machineFullName)));
        String r12_machineFullName = r12_machineFullNameArray.get(machine);

        //2
        ArrayList<String> r12_machineTorqueArray = new ArrayList<>(Arrays.asList(resources.getStringArray(R.array.machine_torque)));
        int r12_machineTorque = Integer.parseInt(r12_machineTorqueArray.get(machine));

        //3
        ArrayList<String> r12_axialForceArray = new ArrayList<>(Arrays.asList(resources.getStringArray(R.array.machine_axial_force)));
        int r12_axialForce = Integer.parseInt(r12_axialForceArray.get(machine));

        //4
        float r12_m = getTrunc((r10_k/r3_d),1000);

        //5
        float r12_n = getTrunc((r9_B/r3_d), 1000);

        //6
        float r12_F = getTrunc((0.314*(r3_d*r3_d)),1000);

        //7
        float r12_Imin = getTrunc((0.0054 * Math.pow(r3_d, 4)), 1000);

        //8
        float r12_Ib = getTrunc((r11_8_L - r11_7_tableLength), 1000);

        resultCalculation = resultCalculation + getString(R.string.r12_firstPart,
                r12_machineFullName,
                r12_machineTorque + "",
                (r12_axialForce + ""),
                (r12_m + ""),
                (r12_n + ""),
                r12_F + "",
                r12_Imin + "",
                r12_Ib + "");

        //проверки

        //1
        float r12check_1 = getTrunc((3*r12_machineTorque),1000);

        //2
        float r12check_2 = getTrunc(0.026 * Math.pow(10, (1.4*r12_m) + (0.2*r12_n)) * 1650 * Math.pow(r7_q, 3), 10);

        //3
        float r12check_3 = getTrunc(r12_axialForce*3, 1000);

        //4
        float r12check_4 = getTrunc(0.25 * r12_F * 3000, 1000);

        //5
        float r12check_5 = getTrunc(r12_axialForce, 1000);

        //6
        float r12check_6 = getTrunc((1.67 * (Math.PI * Math.PI) * 225000 * r12_Imin) / r12_Ib, 1000);

        resultCalculation = resultCalculation + getString(R.string.r12_secondPart,
                r12check_1 + "",
                r12check_2 + "",
                r12check_3 + "",
                r12check_4 + "",
                r12check_5 + "",
                r12check_6 + "") + "\r\n";

        //conclusion

        if (r12check_1 <= r12check_2){
            if (r12check_3 <= r12check_4){
                if (r12check_5 <= r12check_6){
                    resultCalculation = resultCalculation + getString(R.string.r12_allConditionTrue);
                }else resultCalculation = resultCalculation + getString(R.string.r12_thirdConditionFalse);
            }else resultCalculation = resultCalculation + getString(R.string.r12_secondConditionFalse);
        }else resultCalculation = resultCalculation + getString(R.string.r12_firsConditionFalse);

















        System.out.println("Test output:  " + r2_2f);
        return resultCalculation;

    }

    public String getInitialData(){
        String InitialData = "";
        Resources resources = getResources();
        //-----------------
        //START // Получение всех переменных
        //-----------------
        double holeDiameter;
        if (binding.holeDiameter.getText().toString().length() == 0) {
            holeDiameter = 20.0;
        } else {
            holeDiameter = Double.parseDouble(binding.holeDiameter.getText().toString());
        }


        boolean isFinish = binding.finishingCheckbox.isChecked();
        int isFinishInt = isFinish ? 1 : 0;
        int workpieceMaterial = binding.workpieceSpinner.getSelectedItemPosition();
        int machine = binding.machineSpinner.getSelectedItemPosition();
        double tolerance;
        if (binding.editTolerance.getText().toString().length() == 0)
            tolerance = 0.13;
        else tolerance = Double.parseDouble(binding.editTolerance.getText().toString());

        double toleranceES;
        if (binding.editToleranceEs.getText().toString().length() == 0)
            toleranceES = 0.13;
        else toleranceES = Double.parseDouble(binding.editToleranceEs.getText().toString());

        double toleranceEI;
        if (binding.editToleranceEi.getText().toString().length() == 0)
            toleranceEI = 0.0;
        else toleranceEI = Double.parseDouble(binding.editToleranceEi.getText().toString());

        //-----------------
        //END // Получение всех переменных
        //-----------------

        ArrayList<String> isFinishArray = new ArrayList<>(Arrays.asList(resources.getStringArray(R.array.finishOrSemifinish)));
        String textIsFinish = isFinishArray.get(isFinishInt);
        ArrayList<String> workpieceArray = new ArrayList<>(Arrays.asList(resources.getStringArray(R.array.workpiece_material_array)));
        String textWorkpiece = workpieceArray.get(workpieceMaterial);
        ArrayList<String> machineArray = new ArrayList<>(Arrays.asList(resources.getStringArray(R.array.machine_array)));
        String textMachine = machineArray.get(machine);


        InitialData = getString(R.string.initialValueForm,
                holeDiameter + "",
                textIsFinish,
                textWorkpiece,
                textMachine,
                tolerance + "",
                toleranceES + "",
                toleranceEI + "");


        return InitialData;
    }

    public void doReset(){
        binding.holeDiameter.setText("");
        binding.finishingCheckbox.setChecked(true);
        binding.workpieceSpinner.setSelection(0);
        binding.machineSpinner.setSelection(0);
        binding.editTolerance.setText("");
        binding.editToleranceEs.setText("");
        binding.editToleranceEi.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public float getTrunc(double value, int round) {
        float d = ((float)((int)(value*round)))/round;
        return d;
    }

}