export interface Option {
    label: string;
    value: string;
}

export interface DropdownProps {
    options: Option[];
    value: string;
    onChange: (value: string) => void;
}

export function Dropdown(props: DropdownProps) {
    const { options, value, onChange } = props;

    return (
        <select value={value} onChange={e => onChange(e.target.value)}>
            {options.map(function(option) {
                return (
                    <option key={option.value} value={option.value}>
                        {option.label}
                    </option>
                );
            })}
        </select>
    );
}
