import { Select } from 'antd';
import { useEffect, useState } from 'react';
import axios from 'axios';

export default function ProductSelector({ value = [], onChange }) {
    const [products, setProducts] = useState([]);

    useEffect(() => {
        axios.get('http://localhost:8080/products')
            .then(res => setProducts(res.data))
            .catch(console.error);
    }, []);

    return (
        <Select
            mode="multiple"
            showSearch
            allowClear
            style={{ width: '100%' }}
            placeholder="Выберите товары"
            value={value}
            onChange={onChange}
            options={products.map(p => ({ label: p.name, value: p.id }))}
            filterOption={(input, option) =>
                option?.label.toLowerCase().includes(input.toLowerCase())
            }
        />
    );
}
