use std::fs::File;
use std::io::Read;
use std::iter;
use std::path::Path;

pub fn file_to_string(path: &str) -> String {
    let path = Path::new(path);
    let display = path.display();

    match File::open(&path) {
        Err(why) => panic!("couldn't open {}: {}", display, why),
        Ok(mut file) => {let mut s = String::new();
            match file.read_to_string(&mut s) {
                Err(why) => panic!("couldn't read {}: {}", display, why),
                Ok(_) => s,
            }
        },
    }
}

pub fn folds<Acc, Elem, F, I>(init: &Acc, f: F, coll: I) -> Vec<Acc>
where
    Acc: Clone,
    F: Fn(Acc, Elem) -> Acc,
    I: Iterator<Item = Elem>,
{
    let mut acc = init.clone();
    let first = iter::once(init.clone());

    first
        .chain(coll.map(|elem| {
            let new_acc = f(acc.clone(), elem);
            acc = new_acc;
            acc.clone()
        }))
        .collect::<Vec<Acc>>()
}