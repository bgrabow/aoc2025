use crate::util;

pub enum Direction {
    Left,
    Right,
}

pub struct Rotation {
    pub direction: Direction,
    pub distance: i32,
}

fn rotations(input: &str) -> Vec<Rotation> {
    input
        .trim()
        .lines()
        .map(|s| {
            let (dir_char, dist_str) = s.split_at(1);
            let direction = match dir_char {
                "L" => Direction::Left,
                "R" => Direction::Right,
                _ => panic!("Invalid direction character"),
            };
            let distance: i32 = dist_str.parse().expect("Invalid distance number");
            Rotation {
                direction,
                distance,
            }
        })
        .collect()
}

fn cycle(value: i32, min: i32, max: i32) -> i32 {
    let range_size = max - min + 1;
    let mut adjusted_value = value - min;
    adjusted_value = adjusted_value % range_size;
    if adjusted_value < 0 {
        adjusted_value += range_size;
    }
    adjusted_value + min
}

const DIAL_START: i32 = 50;
const DIAL_MAX: i32 = 99;
const DIAL_MIN: i32 = 0;
const DIAL_RANGE: i32 = DIAL_MAX - DIAL_MIN + 1;

pub fn signed_distance(rot: &Rotation) -> i32 {
    match rot.direction {
        Direction::Left => -rot.distance,
        Direction::Right => rot.distance,
    }
}

pub fn solve_part1(input: &str) -> String {
    let password_num = rotations(input)
        .iter()
        .map(signed_distance)
        .fold(vec![DIAL_START], |acc, x| {
            let last = *acc.last().unwrap();
            let mut new_acc = acc;
            new_acc.push(cycle(last + x, DIAL_MIN, DIAL_MAX));
            new_acc
        })
        .iter()
        .filter(|&x| x == &0)
        .count();

    password_num.to_string()
}

pub fn zeroes_between(from: i32, to: i32) -> i32 {
    if from <= to {
        to.div_euclid(DIAL_RANGE) - from.div_euclid(DIAL_RANGE)
    } else {
        zeroes_between(-from, -to)
    }
}

#[test]
fn zeroes_between_test() {
    // Method invocation via . has higher precedence than negation via - operator.
    assert_eq!((-1i32).div_euclid(DIAL_RANGE), -1);
    assert_eq!(100i32.div_euclid(DIAL_RANGE), 1);
    assert_eq!(99i32.div_euclid(DIAL_RANGE), 0);
    assert_eq!(0i32.div_euclid(DIAL_RANGE), 0);
    assert_eq!(-1i32.div_euclid(DIAL_RANGE), 0);
    assert_eq!(0i32 % DIAL_RANGE, 0);
    assert_eq!(1i32 % DIAL_RANGE, 1);
    assert_eq!(99i32 % DIAL_RANGE, 99);
    assert_eq!(100i32 % DIAL_RANGE, 0);
    assert_eq!(101i32 % DIAL_RANGE, 1);
    assert_eq!((-1i32) % DIAL_RANGE, -1);
    assert_eq!((-1i32).rem_euclid(DIAL_RANGE), 99);
    assert_eq!((-100i32).rem_euclid(DIAL_RANGE), 0);
    assert_eq!(1i32 % DIAL_RANGE, 1);
    assert_eq!(99i32 % DIAL_RANGE, 99);
    assert_eq!(100i32 % DIAL_RANGE, 0);
    assert_eq!(101i32 % DIAL_RANGE, 1);
    assert_eq!((-1i32) % DIAL_RANGE, -1);

    assert_eq!(zeroes_between(0, 0), 0);
    assert_eq!(zeroes_between(10, 15), 0);
    assert_eq!(zeroes_between(-1, 0), 1);
    assert_eq!(zeroes_between(99, 0), 1);
    assert_eq!(zeroes_between(100, 99), 0);
    assert_eq!(zeroes_between(0, 1), 0);
    assert_eq!(zeroes_between(0, 100), 1);
    assert_eq!(zeroes_between(50, 100), 1);
    assert_eq!(zeroes_between(150, 300), 2);
    assert_eq!(zeroes_between(-150, -300), 2);
    assert_eq!(zeroes_between(-150, 0), 2);
    assert_eq!(zeroes_between(150, -300), 5);
    assert_eq!(zeroes_between(-150, 300), 5);
}

pub fn solve_part2(input: &str) -> String {
    let total_clicks = util::folds(
        &DIAL_START,
        |acc, x| acc + x,
        rotations(input).iter().map(signed_distance),
    )
    .windows(2)
    .map(|w| zeroes_between(w[0], w[1]))
    .fold(0, |a, b| a + b);

    total_clicks.to_string()
}

#[test]
fn part2_test() {
    assert_eq!(
        solve_part2(&util::file_to_string("resources/input_01.txt")),
        "5820"
    );
}
