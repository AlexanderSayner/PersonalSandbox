-- Create Delivery_Methods table
CREATE TABLE IF NOT EXISTS delivery_methods (
    method_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT
);

-- Create Pricing_Rules table
CREATE TABLE IF NOT EXISTS pricing_rules (
    rule_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    method_id UUID NOT NULL,
    min_distance INTEGER,
    max_distance INTEGER,
    min_weight DECIMAL(10, 3),
    max_weight DECIMAL(10, 3),
    cost DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (method_id) REFERENCES delivery_methods(method_id)
);

-- Insert default delivery methods
INSERT INTO delivery_methods (name, description) VALUES 
('Standard', 'Standard delivery method with regular shipping timeframe'),
('Express', 'Express delivery method with faster shipping timeframe'),
('Overnight', 'Overnight delivery method with next-day shipping');

-- Insert default pricing rules
INSERT INTO pricing_rules (method_id, min_distance, max_distance, min_weight, max_weight, cost) VALUES 
((SELECT method_id FROM delivery_methods WHERE name = 'Standard'), 0, 10, 0.0, 5.0, 5.99),
((SELECT method_id FROM delivery_methods WHERE name = 'Standard'), 11, 50, 0.0, 10.0, 8.99),
((SELECT method_id FROM delivery_methods WHERE name = 'Express'), 0, 20, 0.0, 5.0, 12.99),
((SELECT method_id FROM delivery_methods WHERE name = 'Express'), 21, 100, 0.0, 15.0, 18.99),
((SELECT method_id FROM delivery_methods WHERE name = 'Overnight'), 0, 50, 0.0, 10.0, 24.99);