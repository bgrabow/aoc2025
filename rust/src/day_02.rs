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
    id / (pow(10i64, (tens_magnitude(id) / 2)))
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

fn invalid_ids_sum(lb: i64, ub: i64) -> i64 {
    let lb_mag = tens_magnitude(lb);
    let ub_mag = tens_magnitude(ub);

    (lb_mag..=ub_mag)
        .map(|mag| {
            if mag % 2 == 0 {
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
            } else {
                0
            }
        })
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

pub(crate) fn solve_part1(x: &str) -> i64 {
    let range_re = Regex::new(r"(?<lb>\d+)-(?<ub>\d+)").unwrap();
    range_re.captures_iter(x).map(|c| {
        let lb = c["lb"].parse::<i64>().unwrap();
        let ub = c["ub"].parse::<i64>().unwrap();
        let sum = invalid_ids_sum(lb, ub);
        sum
    }).fold(0, |a, b| a + b)
}

#[test]
fn solve_part1_test() {
    assert_eq!(solve_part1(&util::file_to_string("resources/input_02_example.txt")), 1227775554);
}

pub(crate) fn solve_part2(_p0: &str) -> String {
    todo!()
}
