// noinspection JSUnusedGlobalSymbols
function validate(target, expression, validHelp, invalidHelp) {
    const [help] = target.parentElement.parentElement.getElementsByClassName('help');
    if (expression.apply()) {
        target.classList.remove('is-danger');
        if (help) {
            help.classList.remove('is-danger');
            help.textContent = validHelp;
        }
    } else {
        target.classList.add('is-danger');
        if (help) {
            help.classList.add('is-danger');
            help.textContent = invalidHelp;
        }
    }
}
