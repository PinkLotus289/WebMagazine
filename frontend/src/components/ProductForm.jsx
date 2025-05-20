import { Modal, Form, Input, InputNumber, message } from 'antd';
import axios from 'axios';
import { useEffect } from 'react';

export default function ProductForm({ visible, onCancel, onSuccess, product }) {
    const [form] = Form.useForm();

    useEffect(() => {
        if (product) {
            form.setFieldsValue(product);
        } else {
            form.resetFields();
        }
    }, [product, form]);

    const handleSubmit = () => {
        form.validateFields().then(values => {
            const request = product
                ? axios.put(`http://localhost:8080/products/${product.id}`, values)
                : axios.post('http://localhost:8080/products', values);

            request.then(() => {
                message.success(product ? 'Обновлено' : 'Создано');
                onSuccess();
            }).catch(() => message.error('Ошибка при сохранении'));
        });
    };

    return (
        <Modal
            title={product ? 'Редактировать товар' : 'Новый товар'}
            open={visible}
            onCancel={onCancel}
            onOk={handleSubmit}
            okButtonProps={{ style: { backgroundColor: '#40e0d0', borderColor: '#40e0d0' } }}
            okText="Сохранить"
            cancelText="Отмена"
        >
            <Form form={form} layout="vertical">
                <Form.Item name="name" label="Название" rules={[{ required: true, message: 'Введите название' }]}>
                    <Input />
                </Form.Item>
                <Form.Item name="price" label="Цена" rules={[{ required: true, message: 'Введите цену' }]}>
                    <InputNumber min={0.01} step={0.01} style={{ width: '100%' }} />
                </Form.Item>
            </Form>
        </Modal>
    );
}
