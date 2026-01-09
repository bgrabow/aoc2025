use std::cmp;
use regex::Regex;
use crate::util;

fn tens_magnitude(x: i64) -> i64 {
    let mut magnitude = 0;
    let mut acc = x;
    while acc >= 1 {
        magnitude += 1;
        acc /= 10;
    }
    magnitude
}

#[test]
fn tens_magnitude_test() {
    assert_eq!(tens_magnitude(-1), 0);
    assert_eq!(tens_magnitude(0), 0);
    assert_eq!(tens_magnitude(1), 1);
    assert_eq!(tens_magnitude(1010), 4);
    assert_eq!(tens_magnitude(1011), 4);
    assert_eq!(tens_magnitude(10110), 5);
    assert_eq!(tens_magnitude(123456), 6);
}

fn pow(x: i64, n: i64) -> i64 {
    x.pow(n as u32)
}

fn prefix(id: i64) -> i64 {
    id / (pow(10i64, tens_magnitude(id) / 2))
}

#[test]
fn prefix_test() {
    assert_eq!(prefix(10), 1);
    assert_eq!(prefix(1111), 11);
    assert_eq!(prefix(9999), 99);
}

fn repeated(x: i64) -> i64 {
    x * pow(10i64, tens_magnitude(x)) + x
}

#[test]
fn repeated_test() {
    assert_eq!(repeated(1), 11);
    assert_eq!(repeated(10), 1010);
    assert_eq!(repeated(123), 123123);
}

pub fn range_sum(lb_prefix: i64, ub_prefix:i64) -> i64 {
    (lb_prefix..=ub_prefix).map(|x| repeated(x)).sum()
}

pub fn factors(x: i64) -> Vec<i64> {
    (1..=x / 2).rev().filter(|&i| x % i == 0).collect()
}

fn invalid_ids_sum(lb: i64, ub: i64) -> i64 {
    let lb_mag = tens_magnitude(lb);
    let ub_mag = tens_magnitude(ub);
    let ids_sum_in_mag = |mag:i64| -> i64 {
        let lb = cmp::max(lb, pow(10i64, mag - 1));
        let ub = cmp::min(ub, pow(10i64, mag) - 1);

        let lb_prefix = prefix(lb);
        let ub_prefix = prefix(ub);
        let lowest_invalid_id = if repeated(lb_prefix) >= lb { repeated(lb_prefix) } else { repeated(lb_prefix + 1) };
        let highest_invalid_id = if repeated(ub_prefix) <= ub { repeated(ub_prefix) } else { repeated(ub_prefix - 1) };
        if highest_invalid_id < lowest_invalid_id {
            0
        } else {
            range_sum(prefix(lowest_invalid_id), prefix(highest_invalid_id))
        }
    };

    (lb_mag..=ub_mag)
        .filter(|x| -> bool { x % 2 == 0 })
        .map(ids_sum_in_mag)
        .fold(0, |a, b| a + b)
}


#[test]
fn invalid_ids_count_test() {
    assert_eq!(invalid_ids_sum(100, 1000), 0);
    assert_eq!(invalid_ids_sum(100, 1010), 1010);
    assert_eq!(invalid_ids_sum(100, 2021), 1010 + 1111 + 1212 + 1313 + 1414 + 1515 + 1616 + 1717 + 1818 + 1919 + 2020);
    assert_eq!(invalid_ids_sum(10, 12), 11);
    assert_eq!(invalid_ids_sum(10, 20), 11);
    assert_eq!(invalid_ids_sum(12, 20), 0);
}

fn has_repeated_pattern(x: i64) -> bool {
    let mag = tens_magnitude(x);
    if mag == 0 { return false; }

    // Try all divisors of magnitude (number of segments)
    for divisor in 2..=mag {
        if mag % divisor == 0 {
            let segment_length = mag / divisor;
            let segment_value = x / pow(10, mag - segment_length);
            let mut reconstructed = segment_value;

            for _ in 1..divisor {
                reconstructed = reconstructed * pow(10, segment_length) + segment_value;
            }

            if reconstructed == x {
                return true;
            }
        }
    }
    false
}

fn p2_invalid_ids_sum(lb: i64, ub: i64) -> i64 {
    (lb..=ub)
        .filter(|&x| has_repeated_pattern(x))
        .sum()
}

fn p2_invalid_ids_sum_fast(lb: i64, ub: i64) -> i64 {
    let lb_mag = tens_magnitude(lb);
    let ub_mag = tens_magnitude(ub);

    (lb_mag..=ub_mag)
        .map(|mag| {
            // Try all possible segment sizes (number of segments)
            (2..=mag)
                .filter(|&num_segments| mag % num_segments == 0)
                .map(|num_segments| {
                    let segment_size = mag / num_segments;

                    // Find the range of values for this magnitude
                    let mag_lb = cmp::max(lb, pow(10, mag - 1));
                    let mag_ub = cmp::min(ub, pow(10, mag) - 1);

                    // Compute the prefix range for this segment size
                    let lb_prefix = mag_lb / pow(10, mag - segment_size);
                    let ub_prefix = mag_ub / pow(10, mag - segment_size);

                    // For each prefix, construct the repeated value
                    let mut total = 0;
                    for prefix_val in lb_prefix..=ub_prefix {
                        let mut value = prefix_val;
                        for _ in 1..num_segments {
                            value = value * pow(10, segment_size) + prefix_val;
                        }

                        // Check if this value is within bounds
                        if value < mag_lb || value > mag_ub {
                            continue;
                        }

                        // Only count if this is the smallest segment size that creates this pattern
                        let mut is_smallest = true;
                        for smaller_num_segments in 2..num_segments {
                            if mag % smaller_num_segments != 0 {
                                continue;
                            }
                            let smaller_segment_size = mag / smaller_num_segments;
                            let smaller_prefix = value / pow(10, mag - smaller_segment_size);
                            let mut reconstructed = smaller_prefix;
                            for _ in 1..smaller_num_segments {
                                reconstructed = reconstructed * pow(10, smaller_segment_size) + smaller_prefix;
                            }
                            if reconstructed == value {
                                is_smallest = false;
                                break;
                            }
                        }

                        if is_smallest {
                            total += value;
                        }
                    }
                    total
                }).sum::<i64>()
        }).sum::<i64>()
}

fn parse(x: &str) -> Vec<(i64, i64)> {
    let range_re = Regex::new(r"(\d+)-(\d+)").unwrap();
    range_re.captures_iter(x).map(|c| {
        let lb = c[1].parse::<i64>().unwrap();
        let ub = c[2].parse::<i64>().unwrap();
        (lb, ub)
    }).collect()
}

pub(crate) fn solve_part1(x: &str) -> i64 {
    parse(x).into_iter().map(|(lb, ub)| invalid_ids_sum(lb, ub)).sum()
}

#[test]
fn solve_part1_test() {
    assert_eq!(solve_part1(&util::file_to_string("resources/input_02_example.txt")), 1227775554);
}

pub(crate) fn solve_part2(x: &str) -> i64 {
    parse(x).into_iter().map(|(lb, ub)| p2_invalid_ids_sum_fast(lb, ub)).sum()
}

#[test]
fn solve_part2_test() {
    assert_eq!(solve_part2(&util::file_to_string("resources/input_02_example.txt")), 4174379265);
}
