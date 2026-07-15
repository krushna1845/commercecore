package com.krushna.commercecore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class RatingDistributionDTO {
    private int fiveStar;
    private int fourStar;
    private int threeStar;
    private int twoStar;
    private int oneStar;
    private double averageRating;
    private int totalReviews;

    public int getFiveStar() { return fiveStar; }
    public void setFiveStar(int fiveStar) { this.fiveStar = fiveStar; }
    public int getFourStar() { return fourStar; }
    public void setFourStar(int fourStar) { this.fourStar = fourStar; }
    public int getThreeStar() { return threeStar; }
    public void setThreeStar(int threeStar) { this.threeStar = threeStar; }
    public int getTwoStar() { return twoStar; }
    public void setTwoStar(int twoStar) { this.twoStar = twoStar; }
    public int getOneStar() { return oneStar; }
    public void setOneStar(int oneStar) { this.oneStar = oneStar; }
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }
}
