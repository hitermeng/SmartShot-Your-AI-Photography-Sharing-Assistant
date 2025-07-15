package cn.aicamera.backend.dto;

public class ImageAnalysis {
    private Integer grade;
    private Integer moveUpDown;
    private Integer moveLeftRight;
    private Integer moveForwardBackward;

    public ImageAnalysis(Integer grade, Integer moveUpDown, Integer moveLeftRight, Integer moveForwardBackward) {
        this.grade = grade;
        this.moveUpDown = moveUpDown;
        this.moveLeftRight = moveLeftRight;
        this.moveForwardBackward = moveForwardBackward;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Integer getMoveUpDown() {
        return moveUpDown;
    }

    public void setMoveUpDown(Integer moveUpDown) {
        this.moveUpDown = moveUpDown;
    }

    public Integer getMoveLeftRight() {
        return moveLeftRight;
    }

    public void setMoveLeftRight(Integer moveLeftRight) {
        this.moveLeftRight = moveLeftRight;
    }

    public Integer getMoveForwardBackward() {
        return moveForwardBackward;
    }

    public void setMoveForwardBackward(Integer moveForwardBackward) {
        this.moveForwardBackward = moveForwardBackward;
    }
}
