module comparador(
	input wire [7:0] x,
	input wire a,
	input wire b,
	input wire clk,
	input wire reset,
	output wire [7:0] z,
	output wire out
);
	reg [7:0] A;
	reg [1:0] cont;
	reg [7:0] next_A;
	reg [1:0] next_cont;
	localparam
		S0 = 4'b1000,
		S1 = 4'b0100,
		S2 = 4'b0010,
		S3 = 4'b0001;
	reg [3:0] next_state;
	reg [3:0] state;
	always @(posedge clk or negedge reset) begin
		if (!(reset)) begin
			A <= 8'b00000000;
			cont <= 2'b00;
			state <= S0;
		end else begin
			A <= next_A;
			cont <= next_cont;
			state <= next_state;
		end
	end
	always @(x or a or b or A or cont or state) begin
		next_state = state;
		next_A = A;
		next_cont = cont;
		z = 8'b00000000;
		out = 1'b0;
		case(state)
			S0: begin
				next_A = x;
				if (a) begin
					next_state = S1;
				end else if (!(a)) begin
					next_state = S0;
				end
			end
			S1: begin
				if (b) begin
					next_state = S2;
				end else if (!(b)) begin
					next_state = S1;
				end
			end
			S2: begin
				if (mayor) begin
					next_A = x;
				end
				next_cont = 2'b00;
				if (1) begin
					next_state = S3;
				end
			end
			S3: begin
				next_cont = INC(cont);
				out = 1'b1;
				z = A;
				if ((cont==2'b11)) begin
					next_state = S0;
				end else if (!((cont==2'b11))) begin
					next_state = S3;
				end
			end
		endcase
	end
endmodule