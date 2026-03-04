module empty(
	input wire [2:0] a,
	input wire VCC,
	input wire GND,
	input wire clk,
	input wire reset,
	output wire [2:0] led
);
	reg next_isActivated;
	reg isActivated;
	localparam
		S0 = 3'b100,
		S1 = 3'b010,
		S2 = 3'b001;
	reg [2:0] next_state;
	reg [2:0] state;
	always @(posedge clk or negedge reset) begin
		if (!reset) begin
			isActivated <= 1'b0;
			state <= S0;
		end else begin
			isActivated <= next_isActivated;
			state <= next_state;
		end
	end
	always @(a) begin
		next_state = state;
		next_isActivated = isActivated;
		led = 3'b000;
		case(state)
			S0: begin
				if (VCC) begin
					next_state = S1;
				end
			end
			S1: begin
				led = {a[0],a[2],a[1]};
				if (VCC) begin
					next_state = S2;
				end
			end
			S2: begin
				led = {a[1],a[0],a[2]};
				if (VCC) begin
					next_state = S0;
				end
			end
		endcase
	end
endmodule